import zio.Schedule.{exponential, forever, spaced}
import zio.ZIO.logInfo
import zio.{&, Duration, Fiber, RLayer, Scope, Task, TaskLayer, ZIO, ZLayer, durationInt}

class Brain(config: Brain.Config, catalog: Directory, audit: Audit, bus: MessageBus){

  def subscribeToEvents() : ZIO[Scope, Nothing, Fiber.Runtime[Nothing, Unit]] =
    bus
      .subscribe(config.eventTopic)
      .timeout(config.busTimeout)
      .retry(forever && (exponential(100.millis) || spaced(3.seconds)))
      .tap(doSomeWork(_).ignoreLogged)
      .runDrain
      .onTermination(_ => logInfo(s"Shutting down Brain [${hashCode}]"))
      .forkScoped

  private def doSomeWork(msg: String): Task[Unit] =
    logInfo(s"Brain [$hashCode] received [$msg]")
}

object Brain{

  case class Config(
                     busTimeout: Duration = 1.minute,
                     eventTopic: String = "Brain.v1.0",
                     bus: MessageBus.Config = MessageBus.Config(),
                     directory: Directory.Config = Directory.Config(),
                     audit: Audit.Config = Audit.Config()
                   )

  /**
   * Peers vs internals. If you can, always construct your own dependencies.
   * Re-use dependencies only if you have to.
   **/
  def apply(config: Config): TaskLayer[Unit] = {
    MessageBus(config.bus) ++
      Directory(config.directory) ++
      Audit(config.audit) >>>
      ZLayer.fromFunction(new Brain(config, _, _, _)) >>>
      subscribed()
  }

  def live: ZLayer[Config, Throwable, Unit] =
    ZLayer.service[Config].flatMap(env => Brain(env.get))

  def inMemory: RLayer[MessageBus & Directory, Unit] =
    Audit.inMemory >>>
      ZLayer.fromFunction(new Brain(Config(), _, _, _)) >>>
      subscribed()

  private def subscribed(): RLayer[Brain, Unit] =
    ZLayer.service[Brain].flatMap(env => ZLayer.scoped {
      val b = env.get[Brain]
        b.subscribeToEvents() <* logInfo(s"Brain [${b.hashCode}] subscribed")
    }).unit

}
