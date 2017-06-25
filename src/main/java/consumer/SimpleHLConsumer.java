package consumer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;

public class SimpleHLConsumer {


	private final ConsumerConnector consumer;
	private final String topic;

	public SimpleHLConsumer(String zookeeper, String groupId, String topic) {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeper);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "500");
		props.put("zookeeper.sync.time.ms", "250");
		props.put("auto.commit.interval.ms", "1000");

		consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
		this.topic = topic;
	}

	public void testConsumer() {
		Map<String, Integer> topicCount = new HashMap<>();
		topicCount.put(topic, 1);

		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
		List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);
		for (final KafkaStream stream : streams) {
			ConsumerIterator<byte[], byte[]> it = stream.iterator();
			while (it.hasNext()) {
				MessageAndMetadata<byte[], byte[]> data = it.next();
				scala.collection.Iterator<Object> it1 = data.productIterator();
				it1.next();
				it1.next();
				Object esto = it1.next();
				System.out.println(getMessage((Message)esto));
				System.out.println("Message from Single Topic: " + new String(data.message()));

			}
		}
		if (consumer != null) {
			consumer.shutdown();
		}
	}

	public static String getMessage(Message message) {
		ByteBuffer buffer = message.payload();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes);
	}

	public static void main(String[] args) {
		String topic = "test-default";
		SimpleHLConsumer simpleHLConsumer = new SimpleHLConsumer("localhost:2181", "test-couchbase", topic);
		simpleHLConsumer.testConsumer();
	}

}
