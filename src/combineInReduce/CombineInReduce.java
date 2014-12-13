package combineInReduce;

import java.io.File;
import java.io.IOException;
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
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import mandelbrot.Mandelbrot;
import support.CreateImage;

public class CombineInReduce extends Configured implements Tool {
   public static void main(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      int res = ToolRunner.run(new Configuration(), new CombineInReduce(), args);
      
      System.exit(res);
   }

   @Override
   public int run(String[] args) throws Exception {
      System.out.println(Arrays.toString(args));
      Job job = new Job(getConf(), "WordCount");
      job.setJarByClass(CombineInReduce.class);
      
      job.setOutputKeyClass(IntWritable.class);
      job.setOutputValueClass(Text.class);

      job.setMapperClass(Map.class);
      job.setReducerClass(Reduce.class);

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
      private int frame, width, height, zoom, iter, horCenter, verCenter, colShift;
      String pixelOutput;
      private File file;
      
      @Override
      public void map(LongWritable key, Text value, Context context)
              throws IOException, InterruptedException {
          // input should be a single line
    	  // frameX:parameters
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
    	  
    	  

    	  //System.out.println("string0: " + line [0] + " string1: " + line[1]);
    	  frame = Integer.parseInt(line[0].substring(5)); // take out "frame", indexing starts at 0
    	  
    	  new Mandelbrot(width, height, zoom, iter, horCenter, verCenter, colShift, pixelOutput);
    	  new CreateImage(width, height, pixelOutput, pixelOutput + ".png");
    	  file = new File(pixelOutput);
    	  System.out.println("Text file " + pixelOutput + " deleted status: " + file.delete());
    	  //System.out.println("frame set: " + String.valueOf(frame));
    	  System.out.println("key: " + String.valueOf(frame) + " value: " + line[1]);
    	  //context.write(new IntWritable(frame), new Text("Goodbye"));
    	  context.write(new IntWritable(frame), new Text(line[1]));
          //context.write(new IntWritable(frame), new Text(pixelOutput + ".png"));          
         
      }
   }

   public static class Reduce extends Reducer<IntWritable, Text, IntWritable, Text> {
      @Override
      public void reduce(IntWritable frame, Iterable<Text> values, Context context)
              throws IOException, InterruptedException {
    	  
    	  System.out.println("Entered reduce!");
    	  Text finalvalue = new Text();
    	  
          for (Text val : values) {
                 finalvalue = val;
                 // the same key (frame) shouldn't exist twice
                 // this loop only exists because java complained if there was no Iterable
           }
         System.out.println("key: " + frame + "\t value: " + finalvalue);
         context.write(frame, finalvalue);
      }
   }
}
