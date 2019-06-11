package HDFS

import akka.actor.Props
import com.typesafe.config.ConfigFactory
import simakka._
import simakka.statistics.StatCollector
import scala.collection.mutable.ArrayBuffer


object ScenarioManager extends SimConstants{

  def sleep(s: Int): Unit ={
    Thread.sleep( s * 1000)
  }

  def start(): Unit = {
    val configFile = getClass.getClassLoader.
      getResource("application.conf").getFile
    val config = ConfigFactory.parseFile(new java.io.File(configFile))
    val system = akka.actor.ActorSystem("simakka",config)
    val simSystem = system.actorOf(Props[SimSystem], "SimSystem")
    val statCollector = system.actorOf(Props[StatCollector], "StatCollector")
    simSystem ! SetStatCollector(statCollector)
    simSystem ! "Test String Message"
    simSystem ! CreateEntity("CPU","cpu");
    sleep(1);
    simSystem ! CreateEntity("CPUClient","client1");
     simSystem ! CreateEntity("CPUClient","client2");

    simSystem ! CreateEntity("CPUClient","client3");

    simSystem ! CreateEntity("CPUClient","client4");

    simSystem ! CreateEntity("CPUClient","client5");

    simSystem ! CreateEntity("CPUClient","client6");

    simSystem ! CreateEntity("CPUClient","client7");



    sleep(1);

    simSystem ! AddLinkN("cpu", "client1")
    simSystem ! AddLinkN("client1", "cpu")



        simSystem ! AddLinkN("cpu", "client2")
        simSystem ! AddLinkN("client2", "cpu")


        simSystem ! AddLinkN("cpu", "client3")
        simSystem ! AddLinkN("client3", "cpu")


        simSystem ! AddLinkN("cpu", "client4")
        simSystem ! AddLinkN("client4", "cpu")

        simSystem ! AddLinkN("cpu", "client5")
        simSystem ! AddLinkN("client5", "cpu")
        simSystem ! AddLinkN("cpu", "client6")
        simSystem ! AddLinkN("client6", "cpu")
        simSystem ! AddLinkN("cpu", "client7")
        simSystem ! AddLinkN("client7", "cpu")

    sleep(1);
    simSystem ! InitEntity("cpu",null )
    sleep(1);
    simSystem ! InitEntity("client1", Some(ArrayBuffer("cpu")))
    simSystem ! InitEntity("client2", Some(ArrayBuffer("cpu")))
    simSystem ! InitEntity("client3", Some(ArrayBuffer("cpu")))
    simSystem ! InitEntity("client4", Some(ArrayBuffer("cpu")))
    simSystem ! InitEntity("client5", Some(ArrayBuffer("cpu")))
    simSystem ! InitEntity("client6", Some(ArrayBuffer("cpu")))
    simSystem ! InitEntity("client7", Some(ArrayBuffer("cpu")))


    sleep(1);
    //    simSystem ! SimStartTest
  }

  def main(args: Array[String]) {
    start()
  }

}