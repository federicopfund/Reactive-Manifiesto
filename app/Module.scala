import akka.actor.typed.ActorSystem
import com.google.inject.{AbstractModule, Provides, Singleton}
import core._
import services.ReactiveContactAdapter

/**
 * Dependency injection module configuring the reactive actor system
 * 
 * Implements Reactive Manifesto principles:
 * - MESSAGE-DRIVEN: Actor system as foundation
 * - RESILIENT: Supervision strategy for fault tolerance
 * - ELASTIC: Actor system can scale across cores/nodes
 */
class Module extends AbstractModule {

  @Provides
  @Singleton
  def provideActorSystem(): ActorSystem[ContactCommand] =
    ActorSystem(
      ContactEngine.supervised(), // Use supervised version for resilience
      "reactive-contact-system"
    )

  @Provides
  @Singleton
  def provideAdapter(system: ActorSystem[ContactCommand]): ReactiveContactAdapter =
    new ReactiveContactAdapter(system)
}
