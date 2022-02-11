/*
 * No Copyright intended or License applies just for templating.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worldfirst.fxorder.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.worldfirst.fxorder.domain.OrderData;

/**
 * Class OrderRecordsRepository
 * 
 * It provide below five repository functionalities
 * 
 * add() remove() getMatchingOrders() getUnMatchingOrders()
 * 
 * 
 * @author PM
 *
 */

@Repository
public class OrderRecordsRepository {

	/** Data structure that holds the OrderData in memory. */
	ConcurrentHashMap<String, OrderData> repository;

	/**
	 * Data structure that holds the custom hash of all type order to be able to
	 * find out matched orders easily.
	 */
	ConcurrentHashMap<String, String> allOrderHash;

	/**
	 * Data structure that holds the custom hash of ASK type order to be able to
	 * find out matched orders easily.
	 */
	ConcurrentHashMap<String, String> matchingAskOrderHash;

	/**
	 * Data structure that holds the custom hash of BID type order to be able to
	 * find out matched orders easily.
	 */
	ConcurrentHashMap<String, String> matchingBidOrderHash;

	/** Constructor that initializes OrderData data structure in memory. */
	public OrderRecordsRepository() {
		repository = new ConcurrentHashMap<String, OrderData>();
		allOrderHash = new ConcurrentHashMap<String, String>();
		matchingAskOrderHash = new ConcurrentHashMap<String, String>();
		matchingBidOrderHash = new ConcurrentHashMap<String, String>();
	}

	/**
	 * OrderRecordsRepository.add(OrderData)
	 * 
	 * @throws none
	 */
	public String add(OrderData order) {

		repository.put(order.getId(), order);

		if (null == allOrderHash.get(order.toCustomHash())) {
			allOrderHash.put(order.toCustomHash(), order.getId());
		} else {
			String allIds = allOrderHash.get(order.toCustomHash());

			// We are able to hold upto 3000 odd trades for a currency pair
			// having same price and amount which is very much with is range given that FX
			// rates are very volatile in practice
			// but can be altered with high spec machines in production. If it goes beyond
			// this range there is not any severe
			// impact at all - we will just not be able to see the matching trades. They
			// will appear in unmatching trades list instead!

			if (allIds.length() < 150000) {
				allOrderHash.put(order.toCustomHash(), allIds + ":" + order.getId());
			}
		}

		if (order.getOrderType().toString().equalsIgnoreCase("ASK")) {
			if (null != matchingBidOrderHash.get(order.toCustomHash())) {
				if (null == matchingAskOrderHash.get(order.toCustomHash())) {
					matchingAskOrderHash.put(order.toCustomHash(), order.getId());
				} else {
					String allIds = matchingAskOrderHash.get(order.toCustomHash());
					// We are able to hold upto 2000 matching trades for an specific currency pair
					// e.g. GBP/USD
					// which is very reasonable given FX price is very volatile but can be altered
					// with high spec machines in production.
					if (allIds.length() < 90000) {
						matchingAskOrderHash.put(order.toCustomHash(), allIds + ":" + order.getId());
					}

				}
			} else {
				String allIds = allOrderHash.get(order.toCustomHash());
				List<String> list = new ArrayList<String>(Arrays.asList(allIds.split(":")));

				if (list.size() > 1) {
					for (String id : list) {
						if (!id.equals(order.getId())) {
							if (repository.get(id).getOrderType().toString().equalsIgnoreCase("BID")) {

								matchingAskOrderHash.remove(order.toCustomHash());
								matchingBidOrderHash.remove(order.toCustomHash());

								List<String> allAskIds = new ArrayList<String>();
								List<String> allBidIds = new ArrayList<String>();

								for (String nid : list) {
									if (repository.get(nid).getOrderType().toString().equalsIgnoreCase("ASK")) {
										if (allAskIds.size() < 20000) {
											allAskIds.add(nid);
										}
									} else if (repository.get(nid).getOrderType().toString().equalsIgnoreCase("BID")) {
										if (allBidIds.size() < 20000) {
											allBidIds.add(nid);
										}
									}
								}

								matchingAskOrderHash.put(order.toCustomHash(), allAskIds.stream().map(Object::toString)
										.collect(Collectors.joining(":")).toString());
								matchingBidOrderHash.put(order.toCustomHash(), allBidIds.stream().map(Object::toString)
										.collect(Collectors.joining(":")).toString());
								break;
							}
						}
					}
				}
			}

		}

		if (order.getOrderType().toString().equalsIgnoreCase("BID")) {
			if (null != matchingAskOrderHash.get(order.toCustomHash())) {
				if (null == matchingBidOrderHash.get(order.toCustomHash())) {
					matchingBidOrderHash.put(order.toCustomHash(), order.getId());
				} else {
					String allIds = matchingBidOrderHash.get(order.toCustomHash());
					// We are able to hold upto 2000 matching trades for an specific currency pair
					// e.g. GBP/USD
					// which is very reasonable given FX price is very volatile but can be altered
					// with high spec machines in production.
					if (allIds.length() < 90000) {
						matchingBidOrderHash.put(order.toCustomHash(), allIds + ":" + order.getId());
					}

				}
			} else {
				String allIds = allOrderHash.get(order.toCustomHash());
				List<String> list = new ArrayList<String>(Arrays.asList(allIds.split(":")));

				if (list.size() > 1) {
					for (String id : list) {
						if (!id.equals(order.getId())) {
							if (repository.get(id).getOrderType().toString().equalsIgnoreCase("ASK")) {

								matchingAskOrderHash.remove(order.toCustomHash());
								matchingBidOrderHash.remove(order.toCustomHash());

								List<String> allAskIds = new ArrayList<String>();
								List<String> allBidIds = new ArrayList<String>();

								for (String nid : list) {
									if (repository.get(nid).getOrderType().toString().equalsIgnoreCase("ASK")) {
										if (allAskIds.size() < 20000) {
											allAskIds.add(nid);
										}
									} else if (repository.get(nid).getOrderType().toString().equalsIgnoreCase("BID")) {
										if (allBidIds.size() < 20000) {
											allBidIds.add(nid);
										}
									}
								}

								matchingAskOrderHash.put(order.toCustomHash(), allAskIds.stream().map(Object::toString)
										.collect(Collectors.joining(":")).toString());
								matchingBidOrderHash.put(order.toCustomHash(), allBidIds.stream().map(Object::toString)
										.collect(Collectors.joining(":")).toString());
								break;
							}
						}
					}
				}
			}
		}

		return order.getId();
	}

	/**
	 * OrderRecordsRepository.getAllOrders()
	 * 
	 * @throws none
	 */
	public List<OrderData> getAllOrders() {

		return new ArrayList<OrderData>(repository.values());
	}

	/**
	 * OrderRecordsRepository.remove(OrderData)
	 * 
	 * @throws none
	 */
	public String remove(String orderID) {

		OrderData order = repository.get(orderID);

		if (null == order) {
			return null;
		}

		// Remove from main repository
		repository.remove(orderID);

		// Remove from all order hash
		if (null != allOrderHash.get(order.toCustomHash())) {
			String allIds = allOrderHash.get(order.toCustomHash());

			List<String> list = new ArrayList<String>(Arrays.asList(allIds.split(":")));

			if (list.size() > 1) {
				list.remove(list.indexOf(order.getId()));
				String Ids = list.stream().map(Object::toString).collect(Collectors.joining(":")).toString();

				allOrderHash.remove(order.toCustomHash());
				allOrderHash.put(order.toCustomHash(), Ids);
			} else {
				allOrderHash.remove(order.toCustomHash());
			}
		}

		if (order.getOrderType().toString().equalsIgnoreCase("ASK")) {
			if (null != matchingAskOrderHash.get(order.toCustomHash())) {
				String allIds = matchingAskOrderHash.get(order.toCustomHash());

				List<String> list = new ArrayList<String>(Arrays.asList(allIds.split(":")));

				if (list.size() > 1) {
					list.remove(list.indexOf(order.getId()));
					String Ids = list.stream().map(Object::toString).collect(Collectors.joining(":")).toString();

					matchingAskOrderHash.remove(order.toCustomHash());
					matchingAskOrderHash.put(order.toCustomHash(), Ids);
				} else {
					matchingAskOrderHash.remove(order.toCustomHash());

					// Clean up corresponding matching BID orders for this trade
					if (null != matchingBidOrderHash.get(order.toCustomHash())) {
						matchingBidOrderHash.remove(order.toCustomHash());
					}
				}

			}
		} else if (order.getOrderType().toString().equalsIgnoreCase("BID")) {
			if (null != matchingBidOrderHash.get(order.toCustomHash())) {
				String allIds = matchingBidOrderHash.get(order.toCustomHash());

				List<String> list = new ArrayList<String>(Arrays.asList(allIds.split(":")));

				if (list.size() > 1) {
					list.remove(list.indexOf(order.getId()));
					String Ids = list.stream().map(Object::toString).collect(Collectors.joining(":")).toString();

					matchingBidOrderHash.remove(order.toCustomHash());
					matchingBidOrderHash.put(order.toCustomHash(), Ids);
				} else {
					matchingBidOrderHash.remove(order.toCustomHash());

					// Clean up corresponding matching ASK orders for this trade
					if (null != matchingAskOrderHash.get(order.toCustomHash())) {
						matchingAskOrderHash.remove(order.toCustomHash());
					}
				}

			}
		}

		return orderID;
	}

	/**
	 * OrderRecordsRepository.getMatchingOrders()
	 * 
	 * @throws none
	 */
	public List<OrderData> getMatchingOrders() {

		List<OrderData> matchedList = new ArrayList<OrderData>();
		List<String> orderKeys = new ArrayList<String>();

		if (matchingAskOrderHash.size() > 0) {
			for (Map.Entry<String, String> entry : matchingAskOrderHash.entrySet()) {
				if ((null != matchingAskOrderHash.get(entry.getKey()))
						&& (null != matchingBidOrderHash.get(entry.getKey()))) {
					List<String> AskIds = Arrays.asList(entry.getValue().split(":"));
					List<String> BidIds = Arrays.asList(matchingBidOrderHash.get(entry.getKey()).split(":"));
					orderKeys.addAll(AskIds);
					orderKeys.addAll(BidIds);
				}
			}
		}

		for (String key : orderKeys) {
			matchedList.add(repository.get(key));
		}

		return Collections.unmodifiableList(matchedList);
	}

	/**
	 * OrderRecordsRepository.getUnMatchingOrders()
	 * 
	 * @throws none
	 */
	public List<OrderData> getUnMatchingOrders() {
		List<OrderData> unMatchedList = new ArrayList<OrderData>();
		List<String> orderKeys = new ArrayList<String>();

		if (matchingAskOrderHash.size() > 0) {
			for (Map.Entry<String, String> entry : matchingAskOrderHash.entrySet()) {
				if ((null != matchingAskOrderHash.get(entry.getKey()))
						&& (null != matchingBidOrderHash.get(entry.getKey()))) {
					List<String> AskIds = Arrays.asList(entry.getValue().split(":"));
					List<String> BidIds = Arrays.asList(matchingBidOrderHash.get(entry.getKey()).split(":"));
					orderKeys.addAll(AskIds);
					orderKeys.addAll(BidIds);
				}
			}
		}

		for (Map.Entry<String, OrderData> entry : repository.entrySet()) {

			if (!orderKeys.contains(entry.getKey())) {
				unMatchedList.add(repository.get(entry.getKey()));
			}
		}

		return Collections.unmodifiableList(unMatchedList);
	}

}
