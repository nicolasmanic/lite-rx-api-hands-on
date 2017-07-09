package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.BlockingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Learn how to call blocking code from Reactive one with adapted concurrency strategy for
 * blocking code that produces or receives data.
 *
 * For those who know RxJava:
 *  - RxJava subscribeOn = Reactor subscribeOn
 *  - RxJava observeOn = Reactor publishOn
 *  - RxJava Schedulers.io <==> Reactor Schedulers.elastic
 *
 * @author Sebastien Deleuze
 * @see Flux#subscribeOn(Scheduler)
 * @see Flux#publishOn(Scheduler)
 * @see Schedulers
 */
public class Part11BlockingToReactive {

//========================================================================================

	// TODO Create a Flux for reading all users from the blocking repository deferred until the flux is subscribed, and run it with an elastic scheduler
	Flux<User> blockingRepositoryToFlux(BlockingRepository<User> repository) {

		/*
		* Suppose we wish to convert a collection into a Flux. Perhaps obtaining the collection is expensive,
		* so we defer(postpone) looking it up until necessary. In this case, we’ll also specify a Scheduler
		* so that we don’t block our main thread. This scenario is a fast subscriber but slow publisher
		* */

		return Flux.defer(() -> Flux.fromIterable(repository.findAll())).subscribeOn(Schedulers.elastic());
	}

//========================================================================================

	// TODO Insert users contained in the Flux parameter in the blocking repository using an elastic scheduler and return a Mono<Void> that signal the end of the operation
	Mono<Void> fluxToBlockingRepository(Flux<User> flux, BlockingRepository<User> repository) {

		/* In the reverse scenario, suppose we have a fast publisher and a slow subscriber such as storing a record in a database.
		 * Using a Flux for the publisher, we can publish on a separate Scheduler.
		 * Since we’ll be saving data, we’ll just return a Mono<Void> to indicate when the stream has finished processing*/

		return flux.doOnNext(user -> repository.save(user)).publishOn(Schedulers.elastic()).then();

	}

}
