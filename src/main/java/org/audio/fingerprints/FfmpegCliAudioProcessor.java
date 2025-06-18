package org.audio.fingerprints;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FfmpegCliAudioProcessor {
    public static double[] readAudioData(File audioFile) throws IOException {

        File tempFile = File.createTempFile("audio_", ".raw");
        tempFile.deleteOnExit();

        Process process = new ProcessBuilder(
                "ffmpeg",
                "-i", audioFile.getAbsolutePath(),
                "-f", "s16le",
                "-ac", "1",
                "-ar", "44100",
                "-y",
                tempFile.getAbsolutePath()
        ).start();

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFmpeg interrupted", e);
        }


        byte[] bytes = Files.readAllBytes(tempFile.toPath());
        double[] samples = new double[bytes.length / 2];

        for (int i = 0; i < samples.length; i++) {
            short sample = (short) ((bytes[2*i+1] << 8) | (bytes[2*i] & 0xff));
            samples[i] = sample / 32768.0;
        }

        return samples;
    }
}
