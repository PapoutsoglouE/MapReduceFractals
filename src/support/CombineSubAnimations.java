package support;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import com.xuggle.xuggler.Configuration;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import com.coremedia.iso.IsoFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;



// Download Xuggle from: 
// http://xuggle.googlecode.com/svn/trunk/repo/share/java/xuggle/xuggle-xuggler/5.4/xuggle-xuggler-5.4.jar
// documentation at: http://www.xuggle.com/public/documentation/java/api/

// Download mp4parser / isoparser from:
// https://github.com/sannies/mp4parser/releases/download/mp4parser-project-1.0.5/isoparser-1.0.5.jar

// see https://code.google.com/p/mp4parser/wiki/AppendTracks for code
/**
 * This class contains methods for combining numerous short <br>
 * videos into a longer one.
 */

// TODO: everything here
// this is a placeholder class copied from FramesToVideo
public class CombineSubAnimations {
// look at https://code.google.com/p/mp4parser/source/browse/trunk/examples/src/main/java/com/googlecode/mp4parser/stuff/DavidAppend.java?r=719


	/**
	 * Take an ArrayList of sub-animation filenames, then sort the files by <br>
	 * filename and make a video with them.
	 * @param frames	a list with all short videos to compose the final video
	 */
	public CombineSubAnimations(ArrayList<String> videoparts) {
		MovieCreator mc = new MovieCreator();
		Movie video = new Movie(); 
		//video = mc.build(Channels.newChannel(getResourceAsStream("/count-video.mp4")));
		Movie audio = new Movie();
		//audio = mc.build(Channels.newChannel(getResourceAsStream("/count-english-audio.mp4")));


		List<Track> videoTracks = video.getTracks();
		video.setTracks(new LinkedList<Track>());

		List<Track> audioTracks = audio.getTracks();

		try {
			for (Track videoTrack : videoTracks) {
				video.addTrack(new AppendTrack(videoTrack, videoTrack));
			}
			for (Track audioTrack : audioTracks) {
				//video.addTrack(new AppendTrack(audioTrack, audioTrack));
			}
		} catch (IOException e){}

		//IsoFile out = new DefaultMp4Builder().build(video);
		//FileOutputStream fos = new FileOutputStream(new File(String.format("output.mp4")));
		//out.getBox(fos.getChannel());
		//fos.close();
	}

}