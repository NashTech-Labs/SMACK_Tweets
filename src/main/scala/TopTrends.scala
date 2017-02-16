
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

import akka.actor.ActorSystem
import com.datastax.spark.connector._

class TopTrends {
  val system = ActorSystem("system")
  system.scheduler.schedule(0 seconds, 10 seconds)(topTrends())

  def topTrends() {
    val rdd = SparkContext.sc.cassandraTable("test", "hastags")
    val hastag = rdd.map(x => (x.getString(0).toLowerCase, 1))
    val tophastag=hastag.reduceByKey(_ + _).map(x=>(x._2,x._1)).sortByKey(false).first()
      println("::::::::::::::::::::::::::::::::::" + tophastag._2 + "+++++++++++++"+tophastag._1)

  }
}
