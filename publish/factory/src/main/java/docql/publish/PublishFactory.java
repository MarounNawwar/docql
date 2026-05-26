package docql.publish;

/**
 * Factory that produces a {@link PublishService}.
 */
public interface PublishFactory {
    PublishService publishService();
}

