import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.HashMap;


/*
export JAVA_HOME=/usr/local/jdk1.8.0_101
export PATH=${JAVA_HOME}/bin:${PATH}
export HADOOP_CLASSPATH=/opt/cloudera/parcels/CDH/lib/hadoop/hadoop-common.jar:/opt/cloudera/parcels/CDH/lib/hadoop-mapreduce/hadoop-mapreduce-client-core.jar

////////// Move class files to new directory called wordCount

///////// Move test data for input data set to new directory called testData

hadoop fs -put MyData/ .
hadoop fs -put myData/ .

jar cvf invertedIndex.jar -C invertedIndex/ .
hadoop fs -rm -r output
hadoop jar invertedIndex.jar HadoopProject  MyData output

hadoop jar invertedIndex.jar HadoopProject  myData output

hadoop fs -getmerge output collectedResults
You can add -nl to enable adding newline char after the end of each file
cat collectedResults
mua31@ric-edge-01.sci.pitt.edu
 */
public class HadoopProject {

    public static class Map extends Mapper<LongWritable,Text,Text,Text> {

//
//        static int counter = 1; // my code
//        static HashMap <String,Integer> ids = new HashMap<>(); // my code

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException,InterruptedException
        {

            /*Get the name of the file using context.getInputSplit()method*/
            String folderName = ((FileSplit) context.getInputSplit()).getPath().getParent().toString();
            folderName = removeSlashes(folderName);
            String fileName = ((org.apache.hadoop.mapreduce.lib.input.FileSplit) context.getInputSplit()).getPath().getName(); // my code


            String line=value.toString();
            //Split the line in words
            String[] words = line.split("\\W|\\s|_"); // My code
            for(String s:words){
                //for each word emit word as key and file name as value
                if(s.length() == 0 ) continue; // if no words just skip
                context.write(new Text(s.toLowerCase()), new Text(fileName + "," + folderName)); // my code
            }
        }
        // Mustafa code
        private String removeSlashes(String folderName) {
            StringBuilder sbToReturn = new StringBuilder();
            for(int i = folderName.length()-1;  i > 0 ; i--){
                if(folderName.charAt(i) == '/') break;
                sbToReturn.append(folderName.charAt(i));
            }
            if(sbToReturn.length() == 0 )return folderName;
            return sbToReturn.reverse().toString();
        }


    }
    public static class Reduce extends
            Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            /*Declare the Hash Map to store File name as key to compute and store number of times the filename is occurred for as value*/
            HashMap hashMap = new HashMap();
            // for doc id
            int count;

            for(Text t:values){
                String str=t.toString();
                // for folder id

                /*Check if file name is present in the HashMap ,if File name is not present then add the Filename to the HashMap and increment the counter by one , This condition will be satisfied on first occurrence of that word*/
                if(hashMap.get(str) != null){
                    count=(int)hashMap.get(str);
                    hashMap.put(str, ++count);
                }else{
                    /*Else part will execute if file name is already added then just increase the count for that file name which is stored as key in the hash map*/
                    hashMap.put(str, 1);
                }
            }
            /* Emit word and [file1→count of the word1 in file1,docID , file2→count of the word1 in file2,docId ………] as output*/
            context.write(key, new Text(hashMap.toString()));

        }

    }


    public static void main(String[] args) throws Exception {
        Configuration conf= new Configuration();
        Job job = new Job(conf,"MyJob");
        //Defining the output key and value class for the mapper
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setJarByClass(HadoopProject.class);
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
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
