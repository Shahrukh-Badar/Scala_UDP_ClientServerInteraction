/**
  * Created by Shah on 5/10/2017.
  */

import java.net.DatagramSocket
import java.net.{DatagramPacket, InetAddress}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}
import java.net.BindException
import myClasses.MsgType;
import com.twitter.chill.ScalaKryoInstantiator
import java.io.ByteArrayOutputStream
import com.esotericsoftware.kryo.io.{Input, Output}
import java.lang.reflect.Field

import myClasses.{Msg, MsgType}

class _myServer() extends Runnable {
  var _res = "";
  var isClosed: Boolean = false


  def getServerStatus(): Boolean = {
    return isClosed
  }

  def run() {
    var dataSocket: DatagramSocket = null;
    println("Server is Ready");
    try {

      dataSocket = new DatagramSocket(50000);
      var buffer = new Array[Byte](10000);
      isClosed = false

      while (true) {
        buffer = new Array[Byte](1024);
        println("")
        println("Server is waiting for connection(s)...");

        val request: DatagramPacket = new DatagramPacket(buffer, buffer.length);
        dataSocket.receive(request);

        val instantiator = new ScalaKryoInstantiator
        instantiator.setRegistrationRequired(false)
        val kryo = instantiator.newKryo()

        val input = new Input(buffer)
        val deser = kryo.readObject(input, classOf[Msg])


        var recMesg = ""
        //println(deser.message)
        if (deser.message == null) {
          recMesg = "end"
          _res = "END()"
        }
        else {
          recMesg = deser.message.trim()
        }

        if (recMesg.toLowerCase() == "end") {
          //println("Server recieved END().");
          println("********************************************************")
          println("Server has received message. Below are the details")
          println("Status: Connected, Address: " + request.getAddress().toString() + ", Port: " + request.getPort().toString() + ", Message: END()");
          println("********************************************************")
          return;
        }

        println("********************************************************")
        println("Server has received message. Below are the details")
        println("Status: Connected, Address: " + request.getAddress().toString() + ", Port: " + request.getPort().toString() + ", Message: " + recMesg);
        val sendMesg: String = "Echoed:" + (new String(request.getData()));
        println("********************************************************")
        var sendMsgObj = new Msg(recMesg + " :echoed")
        val output = new Output(1024)
        kryo.writeObject(output, sendMsgObj)
        val mesg_byte_send = output.toBytes()

        val sendMesgByte = sendMesg.getBytes();
        val reply: DatagramPacket = new DatagramPacket(mesg_byte_send, mesg_byte_send.length, request.getAddress(), request.getPort());

        dataSocket.send(reply);

      }

    } catch {
      case ex: BindException => println("Server exception occured :" + ex)
      case ex: Throwable =>
        println("Server Exception: " + ex)
        println("Server is now terminating....");

    } finally {
      if (dataSocket != null) {
        dataSocket.close();
        println("Server has been terminated successfully");
      }

        isClosed = true

    }
  }
}

object myServer {
  def main(args: Array[String]) {
    ///For closing port before new connection
    /*var dataSocket: DatagramSocket = null;
    try{
      dataSocket = new DatagramSocket(50000);
    } catch {
      case ex: BindException => dataSocket.close()
    }
    finally {
      if (dataSocket != null) {
        dataSocket.close()
      }

    }
*/
    ///End
    val _myServerObj = new _myServer()
    _myServerObj.run()
  }

}
