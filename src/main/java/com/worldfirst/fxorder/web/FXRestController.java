/*
 * No Copyright intended or License applies just for templating.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worldfirst.fxorder.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.worldfirst.fxorder.dto.OrderDTO;
import com.worldfirst.fxorder.exception.InvalidInputException;
import com.worldfirst.fxorder.exception.ServiceException;
import com.worldfirst.fxorder.service.SimpleFXTradingService;

/**
 * Rest Controller for FX services.
 *
 * @author PM
 */
@RestController
public class FXRestController {

	private final SimpleFXTradingService service;

	private static final Logger LOGGER = LoggerFactory.getLogger(FXRestController.class);

	@Autowired
	public FXRestController(final SimpleFXTradingService service) {
		this.service = service;
	}

	/**
	 * Stores a new FX order
	 */
	@RequestMapping(value = "/v1/createOrder", method = RequestMethod.POST)
	public ResponseEntity<?> createOrder(@RequestBody(required = true) OrderDTO orderDTO) {

		try {
			boolean status = service.saveOrder(orderDTO.getCurrency(), orderDTO.getPrice(), orderDTO.getBidOrAsk(),
					orderDTO.getAmount());

			if (status) {
				return new ResponseEntity<>("Order saved!", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Order could not be saved!", HttpStatus.BAD_REQUEST);
			}

		} catch (InvalidInputException ei) {
			LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
			return new ResponseEntity<>(ei.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (ServiceException es) {
			LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
			return new ResponseEntity<>(es.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	
	/**
	 * Cancels an existing order
	 */
	
	  @RequestMapping(value = "/v1/cancelOrder/{Id}", method = RequestMethod.GET)
	  public ResponseEntity<?> cancelOrderById(@PathVariable final String Id) {

		  try {
				service.deleteOrder(Id);
				return new ResponseEntity<>("Order cancelled!", HttpStatus.OK);
			} catch (InvalidInputException ei ) {
				LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
	            return new ResponseEntity<>(ei.getMessage(), HttpStatus.BAD_REQUEST);
		    } catch (ServiceException es) {
		    	LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
	            return new ResponseEntity<>(es.getMessage(), HttpStatus.BAD_REQUEST);
		    }

	}
	
	/**
	 * Returns all unmatching FX order
	 */
	@RequestMapping(value = "/v1/unmatchedOrders", method = RequestMethod.GET)
	public ResponseEntity<?> unmatchedOrders() {

		try {
			return new ResponseEntity<>(service.unMatchingOrders(), HttpStatus.OK);
		} catch (InvalidInputException ei ) {
			LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
            return new ResponseEntity<>(ei.getMessage(), HttpStatus.BAD_REQUEST);
	    } catch (ServiceException es) {
	    	LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
            return new ResponseEntity<>(es.getMessage(), HttpStatus.BAD_REQUEST);
	    }


	}
	
	/**
	 * Returns all matching FX order
	 */
	@RequestMapping(value = "/v1/matchedOrders", method = RequestMethod.GET)
	public ResponseEntity<?> matchedOrders() {

		try {
			return new ResponseEntity<>(service.matchingOrders(), HttpStatus.OK);
		} catch (InvalidInputException ei ) {
			LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
            return new ResponseEntity<>(ei.getMessage(), HttpStatus.BAD_REQUEST);
	    } catch (ServiceException es) {
	    	LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
            return new ResponseEntity<>(es.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	}
	

}
