package combineInReduce;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

public class CombineInReduce extends Configured implements Tool {
   public static void main(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      int res = ToolRunner.run(new Configuration(), new CombineInReduce(), args);
      
      System.exit(res);
   }

@Override
   public int run(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      @SuppressWarnings("deprecation")
	  Job job = new Job(getConf(), "CombineInReduce");
      job.setJarByClass(CombineInReduce.class);
      
      job.setOutputKeyClass(IntWritable.class);
      job.setOutputValueClass(Text.class);

      job.setMapperClass(Map.class);
      job.setReducerClass(Reduce.class);
      job.setNumReduceTasks(1);

      //job.setInputFormatClass(TextInputFormat.class);
      job.setInputFormatClass(NLineInputFormat.class);
      NLineInputFormat.setNumLinesPerSplit(job, 1);
      job.setOutputFormatClass(TextOutputFormat.class);

      FileInputFormat.addInputPath(job, new Path(args[0]));
      FileOutputFormat.setOutputPath(job, new Path(args[1]));

      job.waitForCompletion(true);
      
      return 0;
   }
   
   public static class Map extends Mapper<LongWritable, Text, IntWritable, Text> {
      private int width, height, zoom, iter, horCenter, verCenter, colShift;
      //private int frame;
      String pixelOutput;
      private File file;
      
      @Override
      public void map(LongWritable key, Text value, Context context)
              throws IOException, InterruptedException {
          // input should be a single line
    	  // frameX:parameters
    	  if (value.toString().equals("")) return;
    	  String[] line = value.toString().split(":");
    	  String[] parameters = line[1].split(",");
    	  
    	  // Mandelbrot parameters
    	  width = Integer.parseInt(parameters[0]);
    	  height = Integer.parseInt(parameters[1]);
    	  zoom = Integer.parseInt(parameters[2]);
    	  iter = Integer.parseInt(parameters[3]);
    	  horCenter = Integer.parseInt(parameters[4]);
    	  verCenter = Integer.parseInt(parameters[5]);
    	  colShift = 13;
    	  pixelOutput = line[0];
    	  
    	  // Isolate frame number as integer:
    	  //frame = Integer.parseInt(line[0].substring(5)); // take out "frame", indexing starts at 0
    	  
    	  // Create fractal, then write it to a png image.
    	  new Mandelbrot(width, height, zoom, iter, horCenter, verCenter, colShift, pixelOutput);
    	  new CreateImage(width, height, pixelOutput, pixelOutput + ".png");
    	  
    	  // Delete now useless text file with pixel values:
    	  file = new File(pixelOutput);
    	  System.out.println("Text file " + pixelOutput + " deleted status: " + file.delete());
          
    	  // context.write(new IntWritable(frame), new Text(pixelOutput + ".png"));
    	  context.write(new IntWritable(1), new Text(pixelOutput + ".png"));
      }
   }

   public static class Reduce extends Reducer<IntWritable, Text, IntWritable, Text> {
      @Override
      public void reduce(IntWritable frame, Iterable<Text> values, Context context)
              throws IOException, InterruptedException {
    	  Text finalvalue = new Text();
    	  ArrayList<String> framelist = new ArrayList<String>();
          for (Text val : values) {
                 finalvalue = val;
                 framelist.add(val.toString());
                 // the same key (frame) shouldn't exist twice
                 // this loop only exists because java complained if there was no Iterable
           }
          
          //System.out.println("Whole list: " + framelist);
          new FramesToVideo(framelist); // only one Reduce will run, so this is called only once
          System.out.println("Done");
          context.write(frame, finalvalue);
      }
   }
}
