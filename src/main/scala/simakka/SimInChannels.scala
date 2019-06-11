package simakka

/**
  *
  */
class SimInChannels  {


  var localTime = 0.0

  private var numEmptyChannels = 0

  def getNumEmptyChannels = numEmptyChannels


  /*Hold input channels as
  * key: id of SimEntity who is sending events to this instance
  * value: corresponding SimChannel*/
  val channelMap = scala.collection.mutable.Map[Int, SimInChannel]()


  def getLocalTime() = localTime

  /** true of any of the channels where empty **/
  def anyEmpty() = numEmptyChannels > 0


  def nonEmpty() = (channelMap.size > 0 )&& (numEmptyChannels == 0)
           


  /**
    * @param from : id of source SimEntity
    */
  def addLink(from: Int): Unit = {
    assert(!channelMap.contains(from))
    channelMap.put(from, new SimInChannel(from))
    numEmptyChannels += 1
  }


  def addEvent(nm: NullMessage): Boolean = {
    
    
    
    assert(channelMap contains (nm.from))
    val channel = channelMap.get(nm.from).get

    var aa=channel += nm;

    if (!channel.isEmpty()) numEmptyChannels -= 1
  return aa;
  }


  def addEvent(ev: SimEv): Unit = {
    assert(channelMap contains (ev.from))
    val channel = channelMap.get(ev.from).get

    channel += ev
    if (!channel.isEmpty()) numEmptyChannels -= 1
  }

  def probNextEvent(): (Int, SimEvent) = {
    val (minId, minEvent) = channelMap.mapValues(_.front()).minBy(_._2.time)
    (minId, minEvent)
  }

  def extractNextEvent(minId: Int): SimEvent = {
    val minChannel = channelMap.get(minId).get

    val ev = minChannel.getNextEvent()
    if (minChannel.isEmpty()) numEmptyChannels += 1
    ev
  }


  override def toString() = {
    val headTimes = for {i <- channelMap} yield (i._1, i._2.front, i._2.isEmpty())
    s"headTime = $localTime, queue = ${headTimes.mkString("\n")}"
  }

}

object SinInChannels {

  def testSimChannels: Unit = {
    val e1 = SimEv(13, 1, 2, 22, None)




    val n1=NullMessage(14,2,22);
    val n2=NullMessage(65.5,2,22);
    val n3=NullMessage(74,3,22);
    val n4=NullMessage(51,3,22);


    val channels = new SimInChannels()
    channels.addLink(2)
    channels.addLink(3)

    channels.addEvent(e1)



    channels.addEvent(n1);
    channels.addEvent(n2);
    channels.addEvent(n3);
    channels.addEvent(n4);

    // println(channels)
    var (minId, minEvent) = channels.probNextEvent()
    println(minId + "   bbb  "+minEvent)
    println(channels.extractNextEvent(minId))

    var (minId2, minEvent2) = channels.probNextEvent()
    println(minId2 + "   bbb  "+minEvent2)
    println(channels.extractNextEvent(minId2))

    var (minId3, minEvent3) = channels.probNextEvent()
    println(minId3 + "   bbb  "+minEvent3)
    println(channels.extractNextEvent(minId3))

    var (minId4, minEvent4) = channels.probNextEvent()
    println(minId4 + "   bbb  "+minEvent4)
    println(channels.extractNextEvent(minId4))

  }

  def main(args: Array[String]): Unit = {
    println("Run ")
    testSimChannels
  }
}



