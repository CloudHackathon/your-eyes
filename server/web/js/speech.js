var audio_context;
var recorder;
var au;

  function startUserMedia(stream) {
    var input = audio_context.createMediaStreamSource(stream);
    recorder = new Recorder(input);
  }

  function startRecording() {
    recorder && recorder.record();
  }

  function stopRecording() {
    recorder && recorder.stop();
    // create WAV download link using audio data blob
    createDownloadLink();
    
    recorder.clear();
  }
  var Blob;
  function createDownloadLink() {
    recorder && recorder.exportWAV(function(blob) {
      Blob = blob;
      var url = URL.createObjectURL(blob);
      var hf = document.createElement('a');
      au = document.createElement('audio')
      au.controls = false;
      au.src = url;
      hf.href = url;
      hf.download = new Date().toISOString() + '.wav';
      hf.innerHTML = hf.download;
      if($('audio')) {
        $("audio").remove();
      }
      $("body").append(au);
    });
  }
  var timer, s = 0, m = 0;
  function startTime() {
    s += 1;
    if(s >= 60) {
      m += 1;
      s = 0;
    }
    $('.second').html(checkTime(s));
    $('.minute').html(checkTime(m));
    timer = setTimeout('startTime()',1000)
  }

  function checkTime(i) {
    if (i < 10) {
      i = "0" + i;
    }
      return i;
  }

$(function(){

    $.ajax({
      url: '/get-image.php',
      type: 'get',
      dataType: 'json',
      async: true,
      success: function(data) {
        $('.layout-pic img').attr('src', data.data.url);
      },
      error: function(error) {
        console.log(error);
      }
    });
    try {
      // webkit shim
      window.AudioContext = window.AudioContext || window.webkitAudioContext;
      navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia;
      window.URL = window.URL || window.webkitURL;
      
      audio_context = new AudioContext;
    } catch (e) {
      alert('No web audio support in this browser!');
    }
    
    navigator.getUserMedia({audio: true}, startUserMedia, function(e) {});
    var isPlaying = false;

    $('.record-btn, #reset').on('click', function() {
        if(isPlaying) {
          $('audio')[0].pause();
        }
        s = 0;
        m = 0;
        $('.record-tips').hide();
        $('.recording-tips').show();
        $('.submit-tips').hide();
        $('.pause').addClass('play');
        $('.pause').removeClass('pause');
        $('.icon-pause').addClass('icon-play');
        $('.icon-pause').removeClass('icon-pause');
        startRecording();
        startTime();
    });
    $('.finish').on('click', function() {
        $('.recording-tips').hide();
        $('.submit-tips').show();
        stopRecording();
        clearTimeout(timer);
        $('.listen-tips').html('点击试听');
    });
    $('.mod-attr').on('click', '.play', function() {
        $('audio')[0].play();
        isPlaying = true;
        $('.play').addClass('pause');
        $('.play').removeClass('play');
        $('.icon-play').addClass('icon-pause');
        $('.icon-play').removeClass('icon-play');
        $('.listen-tips').html(checkTime(m) + ':' + checkTime(s));
    });

    $('.mod-attr').on('click', '.pause', function() {
        $('audio')[0].pause();
        $('.pause').addClass('play');
        $('.pause').removeClass('pause');
        $('.icon-pause').addClass('icon-play');
        $('.icon-pause').removeClass('icon-pause');
    });

    $('#submit').on('click', function() {            
        var xhr = new XMLHttpRequest();
        xhr.addEventListener('load', function(res) {
          res = JSON.parse(res.target.response);
          console.log(res);
          if(res.message == "SUCCESS") {
            $('.listen-tips').html('录音已发送，可点击试听');
            $('.tips-re-record, .tips-submit').hide();
          }
        });
        xhr.open('post', "/upload-video.php");

        var form = new FormData(document.getElementById("file"));
        form.append("file", Blob);
        xhr.send(form);
    });
    $('audio')[0].bind('ended', function() {
        $('.pause').addClass('play');
        $('.pause').removeClass('pause');
        $('.icon-pause').addClass('icon-play');
        $('.icon-pause').removeClass('icon-pause');
    });

});