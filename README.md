# ☁️ aws lambda prj2epsg service ☁️

## Do this:
1. `mvn package`
1.  Grab `prj2epsg-cli-app-1.0-SNAPSHOT.jar` in `target/` directory (**not** the `original-...` jarfile)
1. Create new lambda function using Java 11 (Corretto); "Author from Scratch"
1. Upload `prj2epsg-cli-app-1.0-SNAPSHOT.jar` jarfile
1. Under "Basic Settings":
    1. update the Handler to: "Handler::handleRequest"
    1. Create a new role from AWS policy templates
    1. wait a few minutes...
1. Create a new test event:
    1. Name it whatever you want
    1. Use this valid json input:
    ``` json
    {
      "terms": "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]"
    }
    ```
1. Test the test event - You'll see some logged output, and the JSON response should include the correct SRID for the given wkt in the test event.
1. Build NEW API Gateway (Choose "REST API") https://console.aws.amazon.com/apigateway/main/apis?region=us-east-1
1. Create resource: "/search" (you can keep the name "search" as well)
1. Create new POST method for "/search" (click the tiny checkmark...)
1. Integration Type should be "Lambda Function", and the Lambda Function should be whatever name was used for the lambda function that was created in the above steps. Everything else can stay default values.
1. Click the "Test" button thing with the lightning bolt
1. In Request Body use the same test json as used previously, and it should work:
```json
{
  "terms": "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]"
}
```
1. Under Actions dropdown -> Deploy API
1. Grab the invoke URL: https://9x15t55ws3.execute-api.us-east-1.amazonaws.com/prod/search
1. Consider requires API token to protect resource.
