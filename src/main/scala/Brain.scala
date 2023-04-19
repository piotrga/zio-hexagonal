import zio.ZIO.logInfo
import zio.{Duration, RIO, RLayer, Scope, Task, TaskLayer, ZLayer, durationInt}

object Brain{

  case class Config(
                     bus: MessageBus.Config = MessageBus.Config(),
                     directory: Directory.Config = Directory.Config(),
                     audit: Audit.Config = Audit.Config(),
                     busTimeout: Duration = 1.minute,
                     eventTopic: String = "Brain.v1.0"
                   )

  /* Peers vs internals. If you can, always construct your own dependencies. Re-use dependencies only if you have to. */
  def apply(config: Config): TaskLayer[Unit] = {
    MessageBus(config.bus) ++
      Directory(config.directory) ++
      Audit(config.audit) >>>
      ZLayer.fromFunction(new Brain(config, _, _, _)) >>>
      subscribed()
  }

  def inMemory: RLayer[MessageBus, Unit] =
    Directory.inMemory ++
      Audit.inMemory >>>
      ZLayer.fromFunction(new Brain(Config(), _, _, _)) >>>
      subscribed()

  private def subscribed(): RLayer[Brain, Unit] = {
    ZLayer.service[Brain].flatMap(env => ZLayer.scoped(
      env.get[Brain]
        .subscribeToEvents()
    ))
  }
}

class Brain(config: Brain.Config, catalog: Directory, audit: Audit, bus: MessageBus){
  def subscribeToEvents() : RIO[Scope, Unit] =
    bus.subscribe(config.eventTopic)
      .timeout(config.busTimeout)
      .tap(doSomeWork(_).ignoreLogged)
      .runDrain
      .onTermination(_ => logInfo("Shutting down Brain"))
      .forkScoped.unit

  private def doSomeWork(msg: String): Task[Unit] =
    logInfo(s"Brain received [$msg]")
}