package org.audio;

public class AudioConfig {
    public static final int SAMPLE_RATE = 44100;
    public static final int SAMPLE_SIZE_IN_BITS = 16;
    public static final int CHANNELS = 1;
}

// curl -X POST -F "trackId=track123" -F "title=track1" -F "audioFile=@attack.mp3" http://localhost:8080/api/audio/register
// curl -X POST -F "trackId=track2" -F "title=track2" -F "audioFile=@attack_acoustic.mp3" http://localhost:8080/api/audio/register

// curl -X POST -F "audioFile=@pitched.mp3" http://localhost:8080/api/audio/identify
// curl -X POST -F "audioFile=@pitched.mp3" "http://localhost:8080/api/audio/top-similar?limit=3&minConfidence=0.01"