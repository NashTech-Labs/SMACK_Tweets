import java.lang.String
import java.util.Properties

import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

case class Tweet(userName: String, text: String)

object KafkaProducerApp {

  val KafkaTopic = "tweets"

  val conf = ConfigFactory.load()
  val kafkaProducer = {
    val props = new Properties()
    props.put("request.required.acks", "1")
    props.put("bootstrap.servers","localhost:9092")
    props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer")
    new KafkaProducer[String, String](props)
  }

  def main(args: Array[String]) {
    val twitterConf = new ConfigurationBuilder()
      .setOAuthConsumerKey(conf.getString("twitter.consumerKey"))
      .setOAuthConsumerSecret(conf.getString("twitter.consumerSecret"))
      .setOAuthAccessToken(conf.getString("twitter.accessToken"))
        .setOAuthAccessTokenSecret(conf.getString("twitter.accessTokenSecret"))
      .build()
    val twitterStream = new TwitterStreamFactory(twitterConf).getInstance()
    twitterStream.addListener(new OnTweetPosted(s =>sendToKafka(toTweet(s))))
    twitterStream.sample("en")
  }

  private def toTweet(s: Status): Tweet = {
    Tweet(s.getUser.getName, s.getText)
  }

  private def sendToKafka(t: Tweet) {
    val msg = new ProducerRecord[String, String](KafkaTopic, t.text)
    kafkaProducer.send(msg)
  }

}

class OnTweetPosted(cb: Status => Unit) extends StatusListener {

  override def onStatus(status: Status): Unit = cb(status)

  override def onException(ex: Exception): Unit = throw ex

  // no-op for the following events
  override def onStallWarning(warning: StallWarning): Unit = {}

  override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}

  override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}

  override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}
}