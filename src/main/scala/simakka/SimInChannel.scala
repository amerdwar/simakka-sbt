package simakka

/**
  * 
  */
class SimInChannel(val id: Int) {
  /** This channel can advance its time safely to the lastNullMessageTime,
    * nullMessages update the maxTime to its time */

  var lastNullMessage: SimEvent = SimEvNone

  var lastAddedEventTime = 0.0

  var localChannelTime = 0.0


  /** holds only SimEv types, nullMessages are not stored here */
  val queue = scala.collection.mutable.Queue[SimEv]()


  //  def empty() = queue.isEmpty && localChannelTime >= recentSourceTime
  def isEmpty() = queue.isEmpty && lastNullMessage == SimEvNone


  def front(): SimEvent =
    if (!queue.isEmpty) {
      queue.front
    } else {
      lastNullMessage
    }


  def getNextEvent(): SimEvent = {
    if (isEmpty()) {
println("  gg is embty  "+queue.isEmpty +"   null message "+lastNullMessage.time);
      return SimEvNone

    }


    if (queue.isEmpty) {
      val result = lastNullMessage
      localChannelTime = result.time
       lastNullMessage = SimEvNone
      return result
    } else {

      assert(localChannelTime <= queue.front.time)

      val result = queue.dequeue()
      localChannelTime = result.time
      return result
    }
  }

  def +=(nm: NullMessage): Boolean = {
    var a:Boolean =addEvent(nm)
    return  a;
  }

  def +=(ev: SimEv): Unit = {
    addEvent(ev)
  }


  def addEvent(nm: NullMessage):Boolean = {

    if(nm.time > lastAddedEventTime){
    lastAddedEventTime = nm.time
    lastNullMessage = nm
      println(nm.toString());

      return true;
    }else{
       //  println(" discard the null message because nm.time is "+nm.time+" lastaddedeventtime " +lastAddedEventTime);
      return  false;
    }
  }


  def addEvent(ev: SimEv) = {
    println(ev.toString()+"    eeeee    "+lastAddedEventTime)

    assert(ev.time > lastAddedEventTime,message =      ev.toString())



    lastAddedEventTime = ev.time
    lastNullMessage = SimEvNone
    queue.enqueue(ev)
  }

  override def toString = {
    val result = queue.mkString(", ")
    s"channel id=$id, localTime=$localChannelTime ,lastNullMessage = $lastNullMessage, queue = $result"
  }


}

object SimInChannel {

  def main(args: Array[String]) {
    def testSimInChannel(): Unit = {

      val e1 = SimEv(2, 1, 1, 2, None)
      val e2 = SimEv(3, 1, 1, 2, None)
      val e3 = SimEv(4, 1, 1, 2, None)

      val n1 = NullMessage(2.5, 1, 2)
      val n2 = NullMessage(3.5, 1, 2)
      val n3 = NullMessage(4, 1, 2)

      val list = List(e1, n1, e3, n3)

      val sec = new SimInChannel(77)

      println(s"empty simInChannel : $sec ")

      list.foreach {
        e => e match {

          case i: SimEv => sec += i; println(sec)
          case i: NullMessage => sec += i; println(sec)
          case _ => println("Unknown Message")

        }
      }

      println()
      println()
      println()
      println()

      println(sec.getNextEvent())
      println(sec.getNextEvent())
      println(sec.getNextEvent())

    }

    testSimInChannel()
  }
}

