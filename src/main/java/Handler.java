import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class Handler implements RequestHandler<Map<String,String>, String>{
    Gson gson = new GsonBuilder().create();
    @Override
    public String handleRequest(Map<String,String> event, Context context)
    {
        LambdaLogger logger = context.getLogger();

        logger.log("\n==== WKT: " + wkt);
        logger.log("\n==== LIB ANSWER RETURNED: " + response.toString());
        return gson.toJson({});
    }
}
