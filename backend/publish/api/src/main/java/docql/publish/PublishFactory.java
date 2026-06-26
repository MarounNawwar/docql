package docql.publish;

/** Backend factory that produces a {@link PublishService}. */
public interface PublishFactory {
  PublishService publishService();
}
