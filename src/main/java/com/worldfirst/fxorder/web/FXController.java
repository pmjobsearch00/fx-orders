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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.worldfirst.fxorder.dto.OrderDTO;
import com.worldfirst.fxorder.exception.InvalidInputException;
import com.worldfirst.fxorder.exception.ServiceException;
import com.worldfirst.fxorder.service.SimpleFXTradingService;

/**
 * FXController for GUI 
 *
 * @author PM
 */
@Controller
public class FXController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FXController.class);
	
	@Autowired
	private SimpleFXTradingService service;

	/**
	 * Landing home page
	 */
	@RequestMapping("/")
	public String home() {
    	return "home";
    }	
	
	/**
	 * Page for a new FX order to be created
	 */
	@RequestMapping("/newOrder")
	public String newOrder(Model model){
		model.addAttribute("orderDTO", new OrderDTO());
    	return "newOrder";
    }	
	
	/**
	 * Stores a new FX order
	 */
	@RequestMapping(value = "/saveOrder", method = RequestMethod.POST)
    public String save(OrderDTO orderDTO, BindingResult result, Model model){
		
		model.addAttribute("orderDTO", new OrderDTO());
		
	    if ((null == result) || (null == orderDTO) || (result.hasErrors())) {
	        model.addAttribute("orderMsg", "Order could not be saved!");
	        return "newOrder";
	    } else if ((null == orderDTO.getCurrency()) || (orderDTO.getCurrency().trim().length() < 7)) {
	        model.addAttribute("orderMsg", "Valid Currency pair is mandatory!");
	        return "newOrder";
	    } else if (null == orderDTO.getBidOrAsk()) {
	        model.addAttribute("orderMsg", "Order type is mandatory!");
	        return "newOrder";
	    } else if (orderDTO.getPrice() <= 0) {
	        model.addAttribute("orderMsg", "Valid Price is mandatory!");
	        return "newOrder";
	    } else if (orderDTO.getAmount() <= 0) {
	        model.addAttribute("orderMsg", "Valid Amount is mandatory!");
	        return "newOrder";
	    }
	    
	    try {
	        boolean status = service.saveOrder(orderDTO.getCurrency(), orderDTO.getPrice(), orderDTO.getBidOrAsk(), orderDTO.getAmount());
	        
	        if (status) {
	            model.addAttribute("orderMsg", "Order saved!");
	        } else {
	            model.addAttribute("orderMsg", "Order could not be saved!");
	        }
	        
	    } catch (InvalidInputException ei ) {
	    	LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
            model.addAttribute("orderMsg", ei.getMessage());
	    } catch (ServiceException es) {
	    	LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
            model.addAttribute("orderMsg", es.getMessage());
	    }
	    
        return "newOrder";

    }
	
	/**
	 * Shows all existing FX order
	 */
	@RequestMapping("/cancelOrder")
	public String cancelOrder(Model model) {
		
		try {
			model.addAttribute("allOrders", service.allOrders());
		} catch (InvalidInputException ei ) {
			LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
            model.addAttribute("orderMsg", ei.getMessage());
	    } catch (ServiceException es) {
	    	LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
            model.addAttribute("orderMsg", es.getMessage());
	    }
		
    	return "cancelOrder";
    }	
	
	/**
	 * Deletes an existing FX order
	 */
    @RequestMapping(value = "/cancelOrder/{id}", method = RequestMethod.GET)
    public String deleteOrder(@PathVariable("id") String Id, Model model) {
	
		try {
			service.deleteOrder(Id);
			return "redirect:/cancelOrder";
		} catch (InvalidInputException ei ) {
			LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
            model.addAttribute("orderMsg", ei.getMessage());
	    } catch (ServiceException es) {
	    	LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
            model.addAttribute("orderMsg", es.getMessage());
	    }
		
    	return "cancelOrder";
    }
    
	/**
	 * Returns all unmatching FX order
	 */
	@RequestMapping("/unmatchedOrders")
	public String unmatchedOrders(Model model) {
		
		try {
			model.addAttribute("unmatchedOrders", service.unMatchingOrders());
		} catch (InvalidInputException ei ) {
			LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
            model.addAttribute("orderMsg", ei.getMessage());
	    } catch (ServiceException es) {
	    	LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
            model.addAttribute("orderMsg", es.getMessage());
	    }

    	return "unmatchedOrders";
    }	
	
	/**
	 * Returns all matching FX order
	 */
	@RequestMapping("/matchedOrders")
	public String matchedOrders(Model model) {
		
		try {
			model.addAttribute("matchedOrders", service.matchingOrders());
		} catch (InvalidInputException ei ) {
			LOGGER.debug("++++++++++++++++++++++++++++: " + ei.getMessage());
            model.addAttribute("orderMsg", ei.getMessage());
	    } catch (ServiceException es) {
	    	LOGGER.debug("++++++++++++++++++++++++++++: " + es.getMessage());
            model.addAttribute("orderMsg", es.getMessage());
	    }

    	return "matchedOrders";
    }	
	
	
}
