package ibf2022.batch2.paf.server.controllers;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.batch2.paf.server.models.Comment;
import ibf2022.batch2.paf.server.models.Restaurant;
import ibf2022.batch2.paf.server.services.RestaurantService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path="/api")
public class RestaurantController {
	@Autowired
	private RestaurantService svc;

	// TODO: Task 2 - request handler
	@GetMapping(path="/cuisines")
	public ResponseEntity<String> getCuisines() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		List<String> cuisines = svc.getCuisines();
		Collections.sort(cuisines);
		for (String c : cuisines) {
			if (c.contains("/")) c = c.replace("/", "_");
			builder.add(c);
		}
		
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(builder.build().toString());
	}

	// TODO: Task 3 - request handler
	@GetMapping(path="/restaurants/{cuisine}")
	public ResponseEntity<String> getRestaurantsByCuisine(@PathVariable String cuisine) {
		if (cuisine.contains("_")) cuisine = cuisine.replace("_", "/");
		
		JsonArrayBuilder builder = Json.createArrayBuilder();
		List<Restaurant> restaurantList = svc.getRestaurantsByCuisine(cuisine);

		for (Restaurant r : restaurantList) {
			System.out.println(r);
			JsonObject o = Json.createObjectBuilder()
							.add("restaurantId", r.getRestaurantId())
							.add("name", r.getName())
							.build();
			builder.add(o);
			System.out.println(o.toString());
		}

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(builder.build().toString());
	}

	// TODO: Task 4 - request handler
	@GetMapping(path="/restaurant/{id}")
	public ResponseEntity<String> getRestaurantById(@PathVariable String id) {
		Optional<Restaurant> r = svc.getRestaurantById(id);

		JsonObjectBuilder ob = Json.createObjectBuilder();
		JsonArrayBuilder ab = Json.createArrayBuilder();

		if(Objects.isNull(r)) {
			String errorMsg = "Missing " + id;
			ob.add("error", errorMsg);
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.contentType(MediaType.APPLICATION_JSON)
					.body(ob.build().toString());
		}

		List<Comment> comments = r.get().getComments();
		for (Comment c : comments) { 
			ab.add(Json.createObjectBuilder()
					.add("restaurantId", c.getRestaurantId())
					.add("name", c.getName())
					.add("date", c.getDate())
					.add("comment", c.getComment())
					.add("rating", c.getRating())
					.build());
		}

		ob = Json.createObjectBuilder()
						.add("restaurant_id", r.get().getRestaurantId())
						.add("name", r.get().getName())
						.add("cuisine", r.get().getCuisine())
						.add("address", r.get().getAddress())
						.add("comments", ab);

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(ob.build().toString());
	}

	// TODO: Task 5 - request handler
	@PostMapping(path="/restaurant/comment", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<String> postComment(HttpServletRequest httpRequest) {
		Comment c = new Comment();
		c.setRestaurantId(httpRequest.getParameter("restaurantId"));
		c.setName(httpRequest.getParameter("name"));
		String ratingString = httpRequest.getParameter("rating");
		c.setRating(Integer.parseInt(ratingString));
		c.setComment(httpRequest.getParameter("comment"));
		long dateNow = new Date().getTime();
		c.setDate(dateNow);
		System.out.println(dateNow);
		svc.postRestaurantComment(c);

		return ResponseEntity.status(HttpStatus.CREATED)
					.contentType(MediaType.APPLICATION_JSON)
					.body("");
	}
}
