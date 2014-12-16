package useCombiners;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
//import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import mandelbrot.Mandelbrot;
import support.CreateImage;
import support.FramesToVideo;

public class UseCombiners extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		long startTime, endTime; // for measuring execution time
		startTime = System.currentTimeMillis();
		System.out.println(Arrays.toString(args));
		int res = ToolRunner.run(new Configuration(), new UseCombiners(), args);
		endTime = System.currentTimeMillis();
		System.out.println("Total time: " + (double) (endTime - startTime)
				/ 1000 + " seconds.");
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {

		System.out.println(Arrays.toString(args));
		@SuppressWarnings("deprecation")
		Job job = new Job(getConf(), "CombineInReduce");
		job.setJarByClass(UseCombiners.class);

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setCombinerClass(MyCombiner.class);
		job.setNumReduceTasks(1);

		// job.setInputFormatClass(TextInputFormat.class);
		job.setInputFormatClass(NLineInputFormat.class);
		NLineInputFormat.setNumLinesPerSplit(job, 5);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);

		return 0;
	}

	public static class Map extends
			Mapper<LongWritable, Text, IntWritable, Text> {
		private int width, height, zoom, iter, horCenter, verCenter, colShift;
		// private int frame;
		String pixelOutput;

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// input should be a single line
			// frameX:parameters
			if (value.toString().equals(""))
				return;
			String[] line = value.toString().split(":");
			if (line[0].equals(""))
				return; // input lines can be commented out with a :
			String[] parameters = line[1].split(",");

			// Mandelbrot parameters
			width = Integer.parseInt(parameters[0]);
			height = Integer.parseInt(parameters[1]);
			zoom = Integer.parseInt(parameters[2]);
			iter = Integer.parseInt(parameters[3]);
			horCenter = Integer.parseInt(parameters[4]);
			verCenter = Integer.parseInt(parameters[5]);
			colShift = 7;
			pixelOutput = line[0];
			Mandelbrot pixels = new Mandelbrot(width, height);
			CreateImage image;
			BufferedImage newframe = null;
			String newframeb64 = new String();

			// Isolate frame number as integer:
			// frame = Integer.parseInt(line[0].substring(5)); // take out
			// "frame", indexing starts at 0

			pixels.Mandelbrot2(width, height, zoom, iter, horCenter, verCenter,
					colShift, pixelOutput);
			image = new CreateImage(width, height, pixels.getMandelbrot(),
					pixelOutput + ".png");
			newframe = image.getBufferedImage();
			newframeb64 = CreateImage.encodeToString(newframe, "png");

			context.write(new IntWritable(1), new Text(pixelOutput + ":"
					+ newframeb64));
		}
	}

	// based on
	// http://developersideas.blogspot.gr/2013/08/hadoop-mapreduce-combiner-classes.html
	public final class MyCombiner extends Reducer<IntWritable, Text, IntWritable, Text> {
		@Override
		public void reduce(final IntWritable key, final Iterable<Text> values,
				final Context context) throws IOException, InterruptedException {
			Text finalvalue = new Text();
			ArrayList<String> framelist = new ArrayList<String>();
			String videoTitle = new String();
			for (Text val : values) {
				finalvalue = val;
				framelist.add(val.toString());
				// the same key (frame) shouldn't exist twice
				// this loop only exists because java complained if there was no
				// Iterable
			}
			
			Collections.sort(framelist); // framelist format: frameXXXX:[base64string]
			
			videoTitle = framelist.get(0).split(":",2)[0]; // title of this video part
			
			// System.out.println("Whole list: " + framelist);
			new FramesToVideo(framelist, videoTitle); // only one Reduce will run, so this
											// is called only once
			System.out.println("Done");

			context.write(new IntWritable(1), new Text(videoTitle + ".mp4"));
		}
	}

	public static class Reduce extends
			Reducer<IntWritable, Text, IntWritable, Text> {
		@Override
		public void reduce(IntWritable frame, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			Text finalvalue = new Text();
			ArrayList<String> framelist = new ArrayList<String>();
			for (Text val : values) {
				finalvalue = val;
				framelist.add(val.toString());
				// the same key (frame) shouldn't exist twice
				// this loop only exists because java complained if there was no
				// Iterable
			}

			// System.out.println("Whole list: " + framelist);
			Collections.sort(framelist);
			new FramesToVideo(framelist, "finalvideo"); // only one Reduce will run, so this
											// is called only once
			System.out.println("Done");

			context.write(frame, finalvalue);
		}
	}

}
