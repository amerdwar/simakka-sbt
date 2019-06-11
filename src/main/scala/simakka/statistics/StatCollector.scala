package simakka.statistics

import java.io.{FileWriter, PrintWriter}
import java.io.FileOutputStream

import akka.actor.Actor
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.event.LoggingReceive

/**
 * 
 */
 class StatCollector extends Actor  with ActorLogging  {

    override def receive: Receive =  {




    case sm: StatValueM =>{
     log.info(sm.csvFormat)

      //val pw = new PrintWriter(new FileWriter("a.txt"),true);

      //pw.println(sm.csvFormat);

    }
    }
}