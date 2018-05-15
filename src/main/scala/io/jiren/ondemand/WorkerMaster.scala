package io.jiren.ondemand

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, Terminated}
import io.jiren.ondemand.WorkerMaster.{FetchWorker, ReachedWorkerLimit, WorkerFetch}

import scala.collection.mutable

/** A companion object to [[WorkerMaster]]. */
object WorkerMaster {
  case class FetchWorker(client: ActorRef)

  case class WorkerFetch(worker: ActorRef)
  case object ReachedWorkerLimit

  def props(limit: Int, fileCache: ActorRef) =
    Props(new WorkerMaster(limit, fileCache))
}

/** Supervises over [[Worker]] actors.
  * @author Sino
  */
final class WorkerMaster(limit: Int, fileCache: ActorRef) extends Actor {
  val workers = mutable.Set[ActorRef]()

  override def supervisorStrategy = OneForOneStrategy() {
    case _: Exception => Restart
  }

  override def receive = {
    case FetchWorker(client) =>
      if (workers.size < limit) {
        val worker = context.actorOf(Worker.props(client, fileCache))

        context.watch(worker)
        workers += worker

        client ! WorkerFetch(worker)
      } else {
        client ! ReachedWorkerLimit
      }

    case Terminated(worker) =>
      workers -= worker
  }
}
