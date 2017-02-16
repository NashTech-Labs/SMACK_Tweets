
import com.datastax.spark.connector._
import com.datastax.spark.connector.streaming._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}


case class Tweets(tweet:String)

object SparkKafka extends App {

  val ssc = new StreamingContext(SparkContext.sc, Seconds(10))
  val kafkaStream = KafkaUtils
    .createStream(ssc, "localhost:2181", "spark_smack", Map("tweets" -> 1))
  val words=kafkaStream.flatMap(x=>x._2.split(" "))
  val hashTag=words.map((x=>Tuple1(x))).filter(x=>x._1.startsWith("#"))
  hashTag.saveToCassandra("test","hastags",SomeColumns("hastag"))
  val tweets=kafkaStream.map(x=>Tweets(x._2))
  tweets.saveToCassandra("test","tweets",SomeColumns("tweet"))

  new TopTrends().topTrends()

  ssc.start()
  ssc.awaitTermination()
}


object SparkContext {
  val conf = new SparkConf().setMaster("local[*]").setAppName("spark_smack")
    .set("spark.cassandra.connection.host", "localhost")
  val sc = new SparkContext(conf)
}
