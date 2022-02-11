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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.worldfirst.fxorder.domain.OrderData;
import com.worldfirst.fxorder.domain.OrderType;
import com.worldfirst.fxorder.repository.OrderRecordsRepository;

/**
 * JUnit Test class for FXRestController
 * 
 * @author PM
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FXRestControllerTest {


	@Autowired
	WebApplicationContext context;
	
	@Autowired
	OrderRecordsRepository repository;

	private MockMvc mvc;
	
	String oid1 = null;
	String oid2 = null;
	String oid3 = null;
	String oid4 = null;

	@Before
	public void setUp() {
		
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
		
		OrderData order1 = new OrderData(UUID.randomUUID().toString(), "GBP/USD", (float)2.2222, 2000L, OrderType.ASK,
				LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		OrderData order2 = new OrderData(UUID.randomUUID().toString(), "GBP/USD", (float)3.3333, 6000L, OrderType.BID,
				LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		OrderData order3 = new OrderData(UUID.randomUUID().toString(), "GBP/USD", (float)2.2222, 2000L, OrderType.BID,
				LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		OrderData order4 = new OrderData(UUID.randomUUID().toString(), "GBP/USD", (float)3.3334, 6000L, OrderType.ASK,
				LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		
		oid1 = repository.add(order1);
		oid2 = repository.add(order2);
		oid3 = repository.add(order3);
		oid4 = repository.add(order4);
		
	}

	@After
	public void cleanUp() {
		repository = null;
	}
	
	
	/**
	 * Test method for
	 * {@link com.sky.library.FXRestController#unMatchedOrders()}. 
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldRetrieveNotMachingOrders() throws Exception {
		
		this.mvc.perform(get("/v1/unmatchedOrders")).andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString(oid2)))
		.andExpect(content().string(containsString(oid4)))
		.andExpect(content().string(not(containsString(oid1))))
		.andExpect(content().string(not(containsString(oid3))));
	}
	
	
	/**
	 * Test method for
	 * {@link com.sky.library.FXRestController#matchedOrders()}. 
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldRetrieveMachingOrders() throws Exception {
		
		this.mvc.perform(get("/v1/matchedOrders")).andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(containsString(oid1)))
		.andExpect(content().string(containsString(oid3)))
		.andExpect(content().string(not(containsString(oid2))))
		.andExpect(content().string(not(containsString(oid4))));

	}
	
	
	/**
	 * Test method for
	 * {@link com.sky.library.FXRestController#createOrder(OrderDTO)}. When
	 * given a valid Order DTO
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldCreateAnOrderWithValidDTO() throws Exception {

		String orderJson = "{\"currency\":\"GBP/USD\",\"bidOrAsk\":\"ASK\",\"price\":\"5.5555\",\"amount\":\"4000\"}";
		
		 mvc.perform(post("/v1/createOrder").content(orderJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(containsString("Order saved!")));

	}
	
	
	/**
	 * Test method for
	 * {@link com.sky.library.FXRestController#createOrder(OrderDTO)}. When
	 * given an invalid Order DTO with wrong currency pair
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldFailToCreateAnOrderWithWrongCurrencyPair() throws Exception {

		String orderJson = "{\"currency\":\"GBP/EUR\",\"bidOrAsk\":\"ASK\",\"price\":\"5.5555\",\"amount\":\"4000\"}";
		
		 mvc.perform(post("/v1/createOrder").content(orderJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("You have entered invalid currency pair!")));
	}
	
	
	/**
	 * Test method for
	 * {@link com.sky.library.FXRestController#createOrder(OrderDTO)}. When
	 * given an invalid Order DTO with no currency pair
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldFailToCreateAnOrderWithNoCurrencyPair() throws Exception {

		String orderJson = "{\"currency\":\"\",\"bidOrAsk\":\"ASK\",\"price\":\"5.5555\",\"amount\":\"4000\"}";
		
		 mvc.perform(post("/v1/createOrder").content(orderJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("You have entered invalid currency pair!")));
	}
	
	
	/**
	 * Test method for
	 * {@link com.sky.library.FXRestController#createOrder(OrderDTO)}. When
	 * given an invalid Order DTO with negative price
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldFailToCreateAnOrderWithNegativePrice() throws Exception {

		String orderJson = "{\"currency\":\"GBP/USD\",\"bidOrAsk\":\"BID\",\"price\":\"-5.5555\",\"amount\":\"4000\"}";
		
		 mvc.perform(post("/v1/createOrder").content(orderJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("You have entered invalid price range: -5.5555")));
	}
	
	/**
	 * Test method for
	 * {@link com.sky.library.FXRestController#createOrder(OrderDTO)}. When
	 * given an invalid Order DTO with wrong price format
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldFailToCreateAnOrderWithWrongPriceFormat() throws Exception {

		String orderJson = "{\"currency\":\"GBP/USD\",\"bidOrAsk\":\"BID\",\"price\":\"5.55556\",\"amount\":\"400\"}";
		
		 mvc.perform(post("/v1/createOrder").content(orderJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("You have entered invalid price range. The format is x.xxxx: 5.55556")));
	}
	
	/**
	 * Test method for
	 * {@link com.sky.library.FXRestController#createOrder(OrderDTO)}. When
	 * given an invalid Order DTO with no amount
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldFailToCreateAnOrderWithNoAmount() throws Exception {

		String orderJson = "{\"currency\":\"GBP/USD\",\"bidOrAsk\":\"BID\",\"price\":\"5.5555\",\"amount\":\"\"}";
		
		 mvc.perform(post("/v1/createOrder").content(orderJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("You have entered invalid amount range: ")));
	}
	
	
	/**
	 * Test method for cancel order
	 * {@link com.sky.library.FXRestController#cancelOrder(String)}. When
	 * given a valid Order ID
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldCancelOrderWithValidOrderId() throws Exception {
		mvc.perform(get("/v1/cancelOrder/{Id}", oid4)).andDo(print())
				.andExpect(status().isOk()).andExpect(content().string(containsString(
						"Order cancelled!")));
	}
	
	
	/**
	 * Test method for cancel order
	 * {@link com.sky.library.FXRestController#cancelOrder(String)}. When
	 * given an invalid Order ID
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldNotCancelOrderWithInValidOrderId() throws Exception {
		mvc.perform(get("/v1/cancelOrder/{Id}", oid4+"invalid")).andDo(print())
				.andExpect(status().isBadRequest()).andExpect(content().string(containsString(
						"OrderId does not exists!")));
	}
}
