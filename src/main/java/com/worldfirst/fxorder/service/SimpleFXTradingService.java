/*
 * No Copyright intended or License applies just for templating.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worldfirst.fxorder.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worldfirst.fxorder.constants.ServiceConstants;
import com.worldfirst.fxorder.domain.OrderData;
import com.worldfirst.fxorder.domain.OrderType;
import com.worldfirst.fxorder.exception.InvalidInputException;
import com.worldfirst.fxorder.exception.ServiceException;
import com.worldfirst.fxorder.repository.OrderRecordsRepository;

/**
 * Class SimpleFXTradingService
 * 
 * It provide below five service functionalities
 * 
 * saveOrder() deleteOrder() matchingOrders() unMatchingOrders()
 * 
 * It also loads a list of orders in memory whenever instantiated as an initial
 * dependency
 * 
 * @author PM
 *
 */

@Service
public class SimpleFXTradingService implements IFXTradingService {

	@Autowired
	private OrderRecordsRepository repository;

	/**
	 * SimpleFXTradingService.saveOrder(String, float, OrderType, long)
	 * 
	 * @throws InvalidInputException,
	 *             ServiceException
	 */
	@Override
	public boolean saveOrder(String currency, float price, OrderType orderType, long amount) 
			throws InvalidInputException, ServiceException {

		if (!currency.equalsIgnoreCase(ServiceConstants.CURRENCY_PAIR_GBPUSD)) {

			String errMsg = "You have entered invalid currency pair!";
			throw new InvalidInputException(errMsg);
		}
		
		if (null == orderType) {			
			String errMsg = "You have entered invalid order type!";
			throw new InvalidInputException(errMsg);
		} else if ((!orderType.toString().equalsIgnoreCase("ASK")) && (!orderType.toString().equalsIgnoreCase("BID"))){
			String errMsg = "You have entered invalid order type!";
			throw new InvalidInputException(errMsg);
		} 

		validateAmount(amount);
		validatePrice(price);

		try {

			OrderData order = new OrderData(UUID.randomUUID().toString(), currency, price, amount, orderType,
					LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

			repository.add(order);

			return true;

		} catch (Exception e) {
			String errMsg = "Service Exception! "+e.getMessage();
			throw new ServiceException(errMsg);
		}

	}
	
	/**
	 * SimpleFXTradingService.allOrders()
	 * 
	 * @throws InvalidInputException,
	 *             ServiceException
	 */
	@Override
	public List<OrderData> allOrders() throws InvalidInputException, ServiceException {
		
		List<OrderData> orders = null;
		try {
			orders = repository.getAllOrders();
		} catch (Exception e) {
			String errMsg = "Service Exception! "+e.getMessage();
			throw new ServiceException(errMsg);
		}
		return orders;
	}
	
	/**
	 * SimpleFXTradingService.deleteOrder(String)
	 * 
	 * @throws InvalidInputException,
	 *             ServiceException
	 */
	@Override
	public boolean deleteOrder(String id) throws InvalidInputException, ServiceException {
		try {
			String orderId = repository.remove(id);
			
			if (null == orderId) {
				String errMsg = "OrderId does not exists!";
				throw new InvalidInputException(errMsg);
			}
			
		} catch (Exception e) {
			String errMsg = "Service Exception! "+e.getMessage();
			throw new ServiceException(errMsg);
		}
		return true;
	}

	/**
	 * SimpleFXTradingService.matchingOrders()
	 * 
	 * @throws InvalidInputException,
	 *             ServiceException
	 */
	@Override
	public List<OrderData> matchingOrders() throws InvalidInputException, ServiceException {
		List<OrderData> orders = null;
		try {
			orders = repository.getMatchingOrders();
		} catch (Exception e) {
			String errMsg = "Service Exception! "+e.getMessage();
			throw new ServiceException(errMsg);
		}
		return orders;
	}

	/**
	 * SimpleFXTradingService.unMatchingOrders()
	 * 
	 * @throws InvalidInputException,
	 *             ServiceException
	 */
	@Override
	public List<OrderData> unMatchingOrders() throws InvalidInputException, ServiceException {
		List<OrderData> orders = null;
		try {
			orders = repository.getUnMatchingOrders();
		} catch (Exception e) {
			String errMsg = "Service Exception! "+e.getMessage();
			throw new ServiceException(errMsg);
		}
		return orders;
	}

	
	
	// Internal helpers

	/**
	 * Validates the given price is a valid float type.
	 * 
	 * @param float
	 *            value
	 */
	private void validatePrice(float value) {
		if (value < Float.MIN_VALUE || value > Float.MAX_VALUE) {
			throw new InvalidInputException("You have entered invalid price range: " + value);
		}
		
		if (!(value % 1 == 0)) {
			String strLen = String.valueOf(value);
			if (strLen.length() < 6 || strLen.length() > 6) {
				throw new InvalidInputException("You have entered invalid price range. The format is x.xxxx: " + value);
			}
		}
		
	}

	/**
	 * Validates the given amount is a with in range of long data type.
	 * 
	 * @param long
	 *            value
	 */
	private void validateAmount(long value) {
		
		if (value < 1) {
			throw new InvalidInputException("You have entered invalid amount range: " + value);
		}
		
		if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
			throw new InvalidInputException("You have entered invalid amount range: " + value);
		}
	}

}