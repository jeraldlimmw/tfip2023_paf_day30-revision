load data local infile '/Users/jeraldlim/tfip/day30-revision/paf-assessment-b2/database/data.csv' 
into table user
fields terminated by ','
lines terminated by '\n'
ignore 1 rows;