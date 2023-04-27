package ibf2022.batch2.paf.server.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ibf2022.batch2.paf.server.models.Comment;
import ibf2022.batch2.paf.server.models.Restaurant;

@Repository
public class RestaurantRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	// TODO: Task 2 
	// Do not change the method's signature
	/* Write the MongoDB query for this method in the comments below
		db.restaurants.distinct("cuisine")
	*/ 
	public List<String> getCuisines() {
		Query query = new Query();
		query.addCriteria(Criteria.where("cuisine").exists(true));

		return mongoTemplate.findDistinct(query, "cuisine", "restaurants", String.class);
	}

	// TODO: Task 3 
	// Do not change the method's signature
	/* Write the MongoDB query for this method in the comments below
		db.restaurants.aggregate([
			{$match: {cuisine: "Afghan"}},
			{$project: {_id:0, restaurant_id:1, name:1}},
			{$sort: {name: 1}}
		])
	*/ 
	public List<Restaurant> getRestaurantsByCuisine(String cuisine) {
		// Query query = new Query();
		// query.addCriteria(Criteria.where("cuisine").is(cuisine));

		// return mongoTemplate.find(query, Restaurant.class, "restaurants");
		MatchOperation mOp = Aggregation.match(Criteria.where("cuisine").is(cuisine));
		ProjectionOperation pOp = Aggregation.project("name")
				.and("restaurant_id").as("restaurantId")
				.andExclude("_id");
		SortOperation sOp = Aggregation.sort(Sort.by(Direction.ASC, "name"));
		
		Aggregation pipeline = Aggregation.newAggregation(mOp, pOp, sOp);
		AggregationResults<Restaurant> restaurantList = mongoTemplate.aggregate(pipeline, "restaurants", Restaurant.class);

		return (List<Restaurant>) restaurantList.getMappedResults();
	}
	
	// TODO: Task 4 
	// Do not change the method's signature
	/* Write the MongoDB query for this method in the comments below
		db.restaurants.aggregate([
			{$match: {restaurant_id: "40386481"}},
			{	
				$lookup: {
					from: "comments",
					foreignField: "restaurantId",
					localField: "restaurant_id",
					as: "comments"
				}
			},
			{
				$project: {_id:0, restaurant_id:1, name:1, cuisine:1, comments:1, 
				address: {$concat: ['$address.building', ', ', '$address.street', ', ', '$address.zipcode', ', ', '$borough']}}
			}
		])
	*/
	public Optional<Restaurant> getRestaurantById(String id) {
		MatchOperation mOp = Aggregation.match(Criteria.where("restaurant_id").is(id));
		LookupOperation lOp = Aggregation.lookup("comments", "restaurant_id", "restaurantId", "comments");
		ProjectionOperation pOp = Aggregation.project("name",
				"cuisine", "comments")
				.and("restaurant_id").as("restaurantId")
				.and(AggregationExpression.from(MongoExpression.create(
					"$concat: ['$address.building', ', ', '$address.street', ', ', '$address.zipcode', ', ', '$borough']"))).as("address")
				.andExclude("_id");
		
		Aggregation pipeline = Aggregation.newAggregation(mOp, lOp, pOp);
		AggregationResults<Restaurant> restaurantList = mongoTemplate.aggregate(pipeline, "restaurants", Restaurant.class);

		List<Restaurant> rl = (List<Restaurant>) restaurantList.getMappedResults();

		if (rl.size()<1) {
			return null;
		}
		System.out.println(rl.get(0));
		return Optional.of(rl.get(0));
	}

	// TODO: Task 5 
	// Do not change the method's signature
	/* Write the MongoDB query for this method in the comments below
		db.comments.insert({
			restaurant_id: "restaurantId",
			name: "name",
			rating: "1",
			comment: "good",
			date: "date"
		}
	*/
	public void insertRestaurantComment(Comment comment) {		
		mongoTemplate.insert(comment, "comments");
		return;
	}
}
