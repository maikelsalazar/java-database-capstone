## Database settings

## Local environment Setting database settings

```bash
export DATABASE_CONFIG=$(cat <<EOF
DATABASE_URL= {DATABASE_URL}
USERNAME= {DATABASE_USERNAME}
PASSWORD= {DATABASE_PASSWORD}
EOF
)
```

*Example:*
```bash
export DATABASE_CONFIG=$(cat <<EOF
DATABASE_URL=jdbc:mysql://localhost:3306/cms?usessl=false
USERNAME=root
PASSWORD=
EOF
)
```

Then run:
```bash
mvn clean install
```

and check the database status

Mongo DB
Installation
Mac OS
```bash
brew install mongodb-community
```

Start:
```bash
brew service start mongodb-community
```

Start Stop:
```bash
brew service start mongodb-community
```

Start Connection using default options
```
mongosh mongodb://localhost:27017
```

Creating or using a collection:

*prescriptions* is the name of the collection:

```bash
use prescriptions;
```
Inserting a prescription



