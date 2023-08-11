package cn.addenda.bc.bc.mc.idfilling;

import cn.addenda.bc.bc.mc.idfilling.idgenerator.snowflake.InetAddressWorkerIdGenerator;
import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/4 19:08
 */
public class InetAddressWorkerIdGeneratorTest {

    @Test
    public void test() {
        InetAddressWorkerIdGenerator inetAddressWorkerIdGenerator = new InetAddressWorkerIdGenerator();
        System.out.println(inetAddressWorkerIdGenerator.workerId());
    }

}
