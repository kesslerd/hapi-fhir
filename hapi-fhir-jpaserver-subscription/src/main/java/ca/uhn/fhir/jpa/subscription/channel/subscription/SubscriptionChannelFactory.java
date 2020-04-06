package ca.uhn.fhir.jpa.subscription.channel.subscription;

/*-
 * #%L
 * HAPI FHIR Subscription Server
 * %%
 * Copyright (C) 2014 - 2020 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ca.uhn.fhir.jpa.subscription.channel.queue.IQueueChannelFactory;
import ca.uhn.fhir.jpa.subscription.channel.queue.IQueueChannelReceiver;
import ca.uhn.fhir.jpa.subscription.channel.queue.IQueueChannelSender;
import ca.uhn.fhir.jpa.subscription.channel.queue.QueueChannelConsumerConfig;
import ca.uhn.fhir.jpa.subscription.model.ResourceDeliveryJsonMessage;
import ca.uhn.fhir.jpa.subscription.model.ResourceModifiedJsonMessage;
import ca.uhn.fhir.jpa.subscription.process.registry.SubscriptionConstants;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;

public class SubscriptionChannelFactory {

	private final IQueueChannelFactory myQueueChannelFactory;

	/**
	 * Constructor
	 */
	public SubscriptionChannelFactory(IQueueChannelFactory theQueueChannelFactory) {
		Validate.notNull(theQueueChannelFactory);
		myQueueChannelFactory = theQueueChannelFactory;
	}

	public IQueueChannelSender newDeliverySendingChannel(String theChannelName) {
		QueueChannelConsumerConfig config = newConfigForDeliveryChannel();
		return myQueueChannelFactory.getOrCreateSender(theChannelName, ResourceDeliveryJsonMessage.class, config);
	}

	public IQueueChannelReceiver newDeliveryReceivingChannel(String theChannelName) {
		QueueChannelConsumerConfig config = newConfigForDeliveryChannel();
		IQueueChannelReceiver channel = myQueueChannelFactory.getOrCreateReceiver(theChannelName, ResourceDeliveryJsonMessage.class, config);
		return new BroadcastingSubscribableChannelWrapper(channel);
	}

	public IQueueChannelSender newMatchingSendingChannel(String theChannelName) {
		QueueChannelConsumerConfig config = newConfigForMatchingChannel();
		return myQueueChannelFactory.getOrCreateSender(theChannelName, ResourceModifiedJsonMessage.class, config);
	}

	public IQueueChannelReceiver newMatchingReceivingChannel(String theChannelName) {
		QueueChannelConsumerConfig config = newConfigForMatchingChannel();
		IQueueChannelReceiver channel = myQueueChannelFactory.getOrCreateReceiver(theChannelName, ResourceModifiedJsonMessage.class, config);
		return new BroadcastingSubscribableChannelWrapper(channel);
	}

	protected QueueChannelConsumerConfig newConfigForDeliveryChannel() {
		QueueChannelConsumerConfig config = new QueueChannelConsumerConfig();
		config.setConcurrentConsumers(getDeliveryChannelConcurrentConsumers());
		return config;
	}

	protected QueueChannelConsumerConfig newConfigForMatchingChannel() {
		QueueChannelConsumerConfig config = new QueueChannelConsumerConfig();
		config.setConcurrentConsumers(getMatchingChannelConcurrentConsumers());
		return config;
	}

	public int getDeliveryChannelConcurrentConsumers() {
		return SubscriptionConstants.DELIVERY_CHANNEL_CONCURRENT_CONSUMERS;
	}

	public int getMatchingChannelConcurrentConsumers() {
		return SubscriptionConstants.MATCHING_CHANNEL_CONCURRENT_CONSUMERS;
	}

	public static class BroadcastingSubscribableChannelWrapper extends AbstractSubscribableChannel implements IQueueChannelReceiver, DisposableBean {

		private final IQueueChannelReceiver myWrappedChannel;

		public BroadcastingSubscribableChannelWrapper(IQueueChannelReceiver theChannel) {
			theChannel.subscribe(message -> send(message));
			myWrappedChannel = theChannel;
		}

		public SubscribableChannel getWrappedChannel() {
			return myWrappedChannel;
		}

		@Override
		protected boolean sendInternal(Message<?> theMessage, long timeout) {
			for (MessageHandler next : getSubscribers()) {
				next.handleMessage(theMessage);
			}
			return true;
		}

		@Override
		public void destroy() throws Exception {
			if (myWrappedChannel instanceof DisposableBean) {
				((DisposableBean) myWrappedChannel).destroy();
			}
		}

		@Override
		public void addInterceptor(ChannelInterceptor interceptor) {
			super.addInterceptor(interceptor);
			myWrappedChannel.addInterceptor(interceptor);
		}


	}
}
