package io.jiren.ondemand

import akka.actor.{ActorSystem, PoisonPill}
import akka.dispatch.{PriorityGenerator, UnboundedStablePriorityMailbox}
import com.typesafe.config.Config
import io.jiren.ondemand.Worker.{PrioritizedAssetRequest, RegularAssetRequest}

/** @author Sino */
final class WorkerMailbox(settings: ActorSystem.Settings, config: Config)
  extends UnboundedStablePriorityMailbox(
    PriorityGenerator {
      case _: PrioritizedAssetRequest => 0
      case _: RegularAssetRequest => 2
      case PoisonPill => 3
      case _ => 1
    })
