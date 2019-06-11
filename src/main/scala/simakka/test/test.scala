package simakka.test

import simakka._
import simakka.distributions._
import akka.remote.transport.AssociationHandle.HandleEvent
import akka.actor.Props

import scala.collection.immutable.Nil
import com.typesafe.config.ConfigFactory
import java.lang.Double

import simakka.statistics._

import scala.collection.mutable.ArrayBuffer

trait SourceTags{
  final val TAG_START_SOURCE= 33
  final val TAG_CUSTOMER_ARRIVE= 44
  final val TAG_CUSTOMER_DEPA= 55
  final val TAG_END_SIMULATION= 66

  
}

class Source( localName: String,  localId: Int) extends SimEntity(localName , localId) with SourceTags{

  var MIN_ARRIVAL_TIME=0.0;
  var MAX_ARRIVAL_TIME=0.0;
  var a=1;
  var randArival:SimRndUniform=null;
  var serverName1="";
  var serverName2="";
  var bool=true;
 override def handleEvent(ev: SimEv): Unit = {
   
 ev.tag match {
   case TAG_START_SOURCE => {

     var s=0.0;
        
    s = randArival.sample();

     if(bool)
    schedule(s,TAG_CUSTOMER_ARRIVE, serverName2,null)//send customer to client
     else
       schedule(s,TAG_CUSTOMER_ARRIVE, serverName1,null)//send customer to client

    bool!=bool;
    if( a < 25){
    a+=1; 
    
    scheduleLocal(s+0.1,TAG_START_SOURCE, null)//send customer to client 
    if(a==25){
        println("***********source statistics***************")
           println
          println("null message num " +nullMessageNum )
          println("event message num " +eventMessageNum )
    println
    println
    
      }
        
      
    }

   }
  
 }
   
 
 }
  
 override def initEntity(data: Option[ArrayBuffer[String]]): Unit = {
   //to do extraxt data from data array
   
   serverName1=data.get(0);
   serverName2=data.get(1);
log.info(serverName2 + serverName1+ "   this is servers");
   MIN_ARRIVAL_TIME=4.0;
   MAX_ARRIVAL_TIME=10.0;
   randArival=  new SimRndUniform("arrival",MIN_ARRIVAL_TIME,MAX_ARRIVAL_TIME);

   scheduleLocal(0.3,TAG_START_SOURCE, null)

   log.info("init is source");

   tick();

 }
    
  
}



class Server( localName: String,  localId: Int) extends SimEntity(localName , localId) with SourceTags{
  var NUM_CUSTOMERS_SERVED=0.0;
  var MIN_SERVICE_TIME=2.0;
  var MAX_SERVICE_TIME=3.0;
  var NUM_LIMITE=20000;
  var IDELE=0;
  var BUSY=1;
  var randService:simakka.distributions.SimRndUniform=null;
  var i=0;
  var startTime=0.0;
  var endTime=0.0;
  //define variable of statistics
  var numDelayedCustomers=0.0;
  var serverStatus=IDELE;
  var numInQ=0.0;
  var totalDelays=0.0;
  var delay=0.0;
  var timeLastEvent=0.0;
  var areaNumInQ = 0.0;
  var areaServerStatus=0.0;
  
  
  var gg=1;
      
  
  
  
  val queue = scala.collection.mutable.Queue[Double]()
  
 override def initEntity(data: Option[ArrayBuffer[String]]): Unit = {
   //to do extraxt data from data array
       NUM_CUSTOMERS_SERVED=10;
       MIN_SERVICE_TIME = 2.0
       MAX_SERVICE_TIME = 10.0

       randService=  new SimRndUniform("service",MIN_SERVICE_TIME,MAX_SERVICE_TIME);
       log.info("init server done");
       
       
     
       statMan.addMeasure(statMan.STAT_USER , "totalDelays")

       statMan.addMeasure(statMan.STAT_USER , "averageDelays")
       statMan.addMeasure(statMan.STAT_USER , "serverUtil")
     statMan.addMeasure(statMan.STAT_USER , "averageQlen")  
       
       
   
 }
  override def handleEvent(ev: SimEv): Unit = {
    
       
 ev.tag match {
   
   case TAG_CUSTOMER_ARRIVE => {

     if(numDelayedCustomers==0){
       startTime=System.nanoTime();
       
     }
   println("arrive at "+ev.time)
   updateStat;  

     //increase the number in q
     if(serverStatus==BUSY){
      numInQ+=1;
      
       if(numInQ>NUM_LIMITE){
         log.info("run out queue")
         
       }else{
         //add to q
         queue.enqueue(ev.time)
        
         
       }
      
     }else{//server is idle
       delay=0; //there is no delay because the customer has been served im
       totalDelays+=delay;
       statMan.updateMeasure("totalDelays",0);
        
       numDelayedCustomers+=1;
    
       if(numDelayedCustomers==NUM_CUSTOMERS_SERVED){
          
           endSimAndPrint
           
          
       
           println("***************** end simulation   **************");
       
         }
        serverStatus=BUSY;
       var ss=randService.sample();
       i=i+1;
       scheduleLocal(ss, TAG_CUSTOMER_DEPA, null)
       
       
       
     }
   }
   
   case TAG_CUSTOMER_DEPA =>{
  //println("Dep at "+ev.time+" num cust ser "+numDelayedCustomers)   
  updateStat

     gg+=1;;


     if(numInQ==0){
       
       
         serverStatus=IDELE;
       
     }else{
       numInQ-=1;

       var de=queue.dequeue();
       delay=simTime-de
       totalDelays+=delay;
       
       statMan.updateMeasure("totalDelays",delay);
       
       var ss=randService.sample();


  
       if(numDelayedCustomers== NUM_CUSTOMERS_SERVED){

         endSimAndPrint

         println
         println("****************** Simulation End  ********************")

         }
       scheduleLocal(ss, TAG_CUSTOMER_DEPA, null)
           
       

     }
     if(numDelayedCustomers== NUM_CUSTOMERS_SERVED){

       endSimAndPrint

     }
   }
   }
 
 }
  
  def updateStat():Unit = {
   var timeSinceLastE = 0.0;
   timeSinceLastE = simTime-timeLastEvent;//here we calculate the time slide 
   timeLastEvent =  simTime;
   areaNumInQ += numInQ*timeSinceLastE;
   
   areaServerStatus += serverStatus*timeSinceLastE;
  
  }
  def endSimAndPrint()={
    println
    println("***************server Statistics******************")
    println
    
   

     /*
    println("toltal delays = "+totalDelays)
    
    println
    println("num delayed customers = "+numDelayedCustomers)
    println("average delay :"+totalDelays/numDelayedCustomers);
    println("average q len :"+areaNumInQ/simTime);
    println("average utlization :"+areaServerStatus/simTime);
    println
    */
      
     statMan.updateMeasure("averageDelays", totalDelays/numDelayedCustomers)
     statMan.updateMeasure("averageQlen", areaNumInQ/simTime)
     statMan.updateMeasure("serverUtil", areaServerStatus/simTime)
  /*  
    println
    println("null message num " +statMan.getMeasure("nullNum") )
    println("event message num " +statMan.getMeasure("eventNum") )
    
    println
    */
     
    endTime=System.nanoTime();
       
    endTime=(endTime-startTime)/NUM_CUSTOMERS_SERVED;
    println
    println("the time is "+endTime)
    
    
    endSimulation

  }
  

}


object ScenarioManager extends SimConstants{



  def sleep(s: Int): Unit ={
    Thread.sleep( s * 1000)
  }

  def startExperiment1(): Unit = {
    val configFile = getClass.getClassLoader.
  getResource("application.conf").getFile
  val config = ConfigFactory.parseFile(new java.io.File(configFile))
  
    
    val system = akka.actor.ActorSystem("simakka",config)
    val simSystem = system.actorOf(Props[SimSystem], "SimSystem")


  //  val simStatCollector = system.actorOf(Props[SimStatCollector], "SimStatCollector")
       val statCollector = system.actorOf(Props[StatCollector], "StatCollector")
//    println( simSystem.path.toStringWithAddress(AddressFromURIString("akka.tcp://simakka@127.0.0.1:6666")));

    simSystem ! SetStatCollector(statCollector)
    simSystem ! "Test String Message"
    //    simSystem ! EndOfSimulation
  //  simSystem ! CreateRemoteEntity("Counter", "counter","akka.tcp://simakka@127.0.0.1:6666")
    
   // simSystem ! CreateEntity("Client", "client")
    
   // simSystem ! AddLinkN("client", "counter")
    
    //simSystem ! InitEntity("client", Some("counter"))
    simSystem ! CreateEntity("Source","source");
   // simSystem ! CreateRemoteEntity("Source", "source","akka.tcp://simakka@127.0.0.1:2552")
    sleep(1);
    simSystem ! CreateEntity("Server","server2");
    simSystem ! CreateEntity("Server","server1");
//    simSystem ! CreateRemoteEntity("Server", "Server","akka.tcp://simakka@127.0.0.1:2552")
    sleep(1);
    
    simSystem ! AddLinkN("source", "server1")
    simSystem ! AddLinkN("server1", "source")
    simSystem ! AddLinkN("source", "server2")
    simSystem ! AddLinkN("server2", "source")



    sleep(1);
    simSystem ! InitEntity("server1",null )
    simSystem ! InitEntity("server2",null )
    simSystem ! InitEntity("source", Some(ArrayBuffer("server1","server2")))
    sleep(1);

    
    sleep(1);
//    simSystem ! SimStartTest
  }

  def main(args: Array[String]) {
    startExperiment1()
  }

}





