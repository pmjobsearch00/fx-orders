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

import java.util.List;

import com.worldfirst.fxorder.domain.OrderData;
import com.worldfirst.fxorder.domain.OrderType;
import com.worldfirst.fxorder.exception.InvalidInputException;
import com.worldfirst.fxorder.exception.ServiceException;

/**
 * Interface IFXTradingService
 * 
 * It provides below four service functionalities
 * 
 * saveOrder()
 * deleteOrder()
 * matchingOrders()
 * unMatchingOrders()
 * 
 * @author PM
 *
 */

public interface IFXTradingService {

	/**
	 * Saves an order.
	 * @param currency price orderType and amount
	 * @return the boolean status
	 * @throws InvalidInputException, ServiceException
	 */
	boolean saveOrder(String currency, float price, OrderType orderType, long amount) 
			throws InvalidInputException, ServiceException;

	/**
	 * Returns the all orders.
	 * @param none
	 * @return the OrderData list
	 * @throws InvalidInputException, ServiceException
	 */
	List<OrderData> allOrders() throws InvalidInputException, ServiceException;

	
	/**
	 * Deletes an order.
	 * @param order id
	 * @return the status
	 * @throws InvalidInputException, ServiceException
	 */
	boolean deleteOrder(String id) 
			throws InvalidInputException, ServiceException;
	
	/**
	 * Returns the matching orders.
	 * @param none
	 * @return the matching OrderData list
	 * @throws InvalidInputException, ServiceException
	 */
	List<OrderData> matchingOrders() throws InvalidInputException, ServiceException;

	/**
	 * Returns the unmatching orders.
	 * @param none
	 * @return the matching OrderData list
	 * @throws InvalidInputException, ServiceException
	 */
	List<OrderData> unMatchingOrders() throws InvalidInputException, ServiceException;
	
}