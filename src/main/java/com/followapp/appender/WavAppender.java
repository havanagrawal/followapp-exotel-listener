package com.followapp.appender;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WavAppender {

	public static File fromUrls(URL... urls) throws UnsupportedAudioFileException, IOException {
		if (urls.length < 2) {
			throw new IllegalArgumentException("Need atleast 2 urls to concatenate audio from.");
		} else {
			AudioInputStream finalStream = joinStreams(AudioSystem.getAudioInputStream(urls[0]),
					AudioSystem.getAudioInputStream(urls[1]));
			
			for (int i = 2; i < urls.length; i++) {
				finalStream = joinStreams(finalStream, AudioSystem.getAudioInputStream(urls[i]));
			}
			
			File currentDirectory = new File(".");
			File tempAudioLocation = new File(currentDirectory.getCanonicalPath() + File.separator + "temp.wav");
			
			AudioSystem.write(finalStream, AudioFileFormat.Type.WAVE, tempAudioLocation);
			return tempAudioLocation;
		}
	}

	private static AudioInputStream joinStreams(AudioInputStream s1, AudioInputStream s2) {
		return new AudioInputStream(new SequenceInputStream(s1, s2), s1.getFormat(),
				s1.getFrameLength() + s2.getFrameLength());
	}
}
