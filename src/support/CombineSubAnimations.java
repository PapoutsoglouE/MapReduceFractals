package support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.coremedia.iso.IsoBufferWrapperImpl;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoOutputStream;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;



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
	public CombineSubAnimations(ArrayList<String> videoparts) throws IOException {
		Movie video = null;
		Movie audio = null;

		video = new MovieCreator().build(new IsoBufferWrapperImpl(readFully(CombineSubAnimations.class.getResourceAsStream("/count-video.mp4"))));

		List<Track> videoTracks = video.getTracks();
		video.setTracks(new LinkedList<Track>());

		for (Track videoTrack : videoTracks) {
			video.addTrack(new AppendTrack(videoTrack, videoTrack));
		}

		IsoFile out = null;
		FileOutputStream fos = null;

		out = (IsoFile) new DefaultMp4Builder().build(video);

		try {
			fos = new FileOutputStream(new File(String.format("output.mp4")));
		} catch (FileNotFoundException e) {
			System.out.println("File not found in CombineSubAnimations");
		}

		BufferedOutputStream bos = new BufferedOutputStream(fos);

		out.getBox(new IsoOutputStream(fos));
		fos.close();


	}


	static byte[] readFully(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int n = 0;
		while (-1 != (n = is.read(buffer))) {
			baos.write(buffer, 0, n);
		}
		return baos.toByteArray();
	}

}