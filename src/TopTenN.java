import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class TopTenN {

    public static class TopNMapper
            extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable one = new IntWritable(1);

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            String line=value.toString();
            //Split the line in words
            String[] words = line.split("\\W|\\s|_"); // My code
            for(String s:words){
                //for each word emit word as key and file name as value
                if(s.length() == 0 ) continue; // if no words just skip
                context.write(new Text(s),one); // my code
            }
        }
    }

    public static class TopNReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
//        private Map<Text, IntWritable> countMap = new HashMap<>();
        private IntWritable result = new IntWritable();


        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context) throws IOException, InterruptedException {
            // computes the number of occurrences of a single word

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }

            // puts the number of occurrences of this word into the map.
            // We need to create another Text object because the Text instance
            // we receive is the same for all the words
            result.set(sum);
            context.write(new Text(key), result);
//            countMap.put(new Text(key), new IntWritable(sum));
        }
//
//        /**
//         * The combiner retrieves every word and puts it into a Map: if the word already exists in the
//         * map, increments its value, otherwise sets it to 1.
//         */
//        public static class TopNCombiner extends Reducer<Text, IntWritable, Text, IntWritable> {
//
//            @Override
//            public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
//
//                // computes the number of occurrences of a single word
//                int sum = 0;
//                for (IntWritable val : values) {
//                    sum += val.get();
//                }
//                context.write(key, new IntWritable(sum));
//            }
//        }
//
//        @Override
//        protected void cleanup(Context context) throws IOException, InterruptedException {
//
//            Map<Text, IntWritable> sortedMap = MiscUtils.sortByValues(countMap);
//
//            int counter = 0;
//            for (Text key : sortedMap.keySet()) {
//                if (counter++ == 20) {
//                    break;
//                }
//                context.write(key, sortedMap.get(key));
//            }
//        }
    }





    public static void main(String[] args) throws Exception {
        Configuration conf= new Configuration();
        Job job = new Job(conf,"MyJob");
        //Defining the output key and value class for the mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setJarByClass(TopTenN.class);
        job.setMapperClass(TopTenN.TopNMapper.class);
        job.setReducerClass(TopTenN.TopNReducer.class);
        //Defining the output value class for the mapper
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        Path outputPath = new Path(args[1]);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, outputPath);
        //deleting the output path automatically from hdfs so that we don't have delete it explicitly
        outputPath.getFileSystem(conf).delete(outputPath);
        //exiting the job only if the flag value becomes false
        System.exit(job.waitForCompletion(true) ? 0 : 1);


    }
}