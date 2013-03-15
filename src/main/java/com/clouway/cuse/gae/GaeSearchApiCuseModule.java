package com.clouway.cuse.gae;

import com.clouway.cuse.spi.*;
import com.google.inject.AbstractModule;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Lazov <ivan.lazov@clouway.com>
 */
public class GaeSearchApiCuseModule extends AbstractModule {



  private List<IndexStrategyLinkingBinder> bindings = new ArrayList<IndexStrategyLinkingBinder>();

  private final Class<? extends EntityLoader> entityLoaderClazz;


  protected GaeSearchApiCuseModule(Class<? extends EntityLoader> entityLoaderClazz) {
    this.entityLoaderClazz = entityLoaderClazz;
  }


  @Override
  protected final void configure() {
    install(new SearchApiCuseBindingModule(entityLoaderClazz));

    configureIndexStrategies();

    TypeLiteral<IndexStrategyMetadata> indexStrategyPairTypeLiteral = new TypeLiteral<IndexStrategyMetadata>() {
    };

    Multibinder<IndexStrategyMetadata> actionHandlerPairMultibinder = Multibinder.newSetBinder(binder(), indexStrategyPairTypeLiteral);

    for (IndexStrategyLinkingBinder binding : bindings) {
      actionHandlerPairMultibinder.addBinding().toInstance(new IndexStrategyMetadata(binding.indexClazz, binding.strategyClazz));

      if (binding.asEagerSingleton) {
        bind(binding.strategyClazz).asEagerSingleton();

      } else if (binding.scope != null) {
        bind(binding.strategyClazz).in(binding.scope);

      } else if (binding.annotation != null) {
        bind(binding.strategyClazz).in(binding.annotation);

      }
    }
  }




  /**
   * Binds action to a handler using <code>embedded domain-specific language</code> (EDSL).
   * <p/>
   * <p/> Here is a typical example of registering a new handler when creating your Guice injector:
   * <pre>
   *
   *  Injector injector =  Guice.createInjector(new DispatchModule() {
   *
   *   protected void configureHandlers() {
   *     dispatch(GetUsersAction.class).through(GetUsersActionHandler.class).in(Singleton.class);
   *   }
   *
   * }
   *
   * </pre>
   */
  protected void configureIndexStrategies() {

  }

  IndexStrategyBindingBuilder strategyBindingBuilder = new IndexStrategyBindingBuilder();

  public final IndexStrategyBinder.HandleThroughtBinding objectIndex(Class<?> strategyClazz) {
    return strategyBindingBuilder.index(strategyClazz);
  }


  class IndexStrategyBindingBuilder implements IndexStrategyBinder {

    public final IndexStrategyBinder.HandleThroughtBinding index(Class<?> indexClazz) {
      IndexStrategyLinkingBinder binding = new IndexStrategyLinkingBinder(indexClazz);
      bindings.add(binding);
      return binding;
    }

  }

  interface IndexStrategyBinder {

    HandleThroughtBinding index(Class<?> indexClazz);

    static interface HandleThroughtBinding {

      ScopedBindingBuilder through(Class<? extends IndexingStrategy<?>> strategyClazz);

    }

  }

  class IndexStrategyLinkingBinder implements IndexStrategyBinder.HandleThroughtBinding, ScopedBindingBuilder {


    private final Class<?> indexClazz;
    private Class<? extends Annotation> annotation;
    Scope scope;
    boolean asEagerSingleton;
    private Class<? extends IndexingStrategy<?>> strategyClazz;

    public IndexStrategyLinkingBinder(Class<?> indexClazz) {
      this.indexClazz = indexClazz;
    }

    public ScopedBindingBuilder through(Class<? extends IndexingStrategy<?>> strategyClazz) {
      this.strategyClazz = strategyClazz;
      return this;
    }

    public void in(Class<? extends Annotation> annotation) {
      this.annotation = annotation;
    }

    public void in(Scope scope) {
      this.scope = scope;
    }

    public void asEagerSingleton() {
      asEagerSingleton = true;
    }

  }


  public class IndexStrategyMetadata {
    private final Class<?> indexClazz;
    private final Class<? extends IndexingStrategy<?>> strategyClazz;

    public IndexStrategyMetadata(Class<?> indexClazz, Class<? extends IndexingStrategy<?>> strategyClazz) {
      this.indexClazz = indexClazz;
      this.strategyClazz = strategyClazz;
    }

    public Class<?> getIndexClazz() {
      return indexClazz;
    }

    public Class<? extends IndexingStrategy<?>> getStrategyClazz() {
      return strategyClazz;
    }
  }
}
