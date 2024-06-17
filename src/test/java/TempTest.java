import org.junit.jupiter.api.Test;
import org.m2sec.common.Log;
import org.m2sec.common.Render;
import org.m2sec.common.models.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class TempTest {

    Log log = new Log(TempTest.class);

    @Test
    public void test() throws IOException {
        Request request = Request.of("http://192.168.1.4:8000/getUserInfo");
        System.out.println("192.168.1.4".equals(request.getHost()));
        System.out.println(
                Render.renderExpression(
                        "request.getHost().equals(\"192.168.1.4\")",
                        new HashMap<>(Map.of("request", request))));
    }
}
