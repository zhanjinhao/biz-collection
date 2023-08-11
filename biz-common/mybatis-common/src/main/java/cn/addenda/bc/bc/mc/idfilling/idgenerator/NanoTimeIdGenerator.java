package cn.addenda.bc.bc.mc.idfilling.idgenerator;

/**
 * @author addenda
 * @since 2022/2/4 14:38
 */
public class NanoTimeIdGenerator implements IdGenerator {

    @Override
    public Object nextId(String scopeName) {
        return System.nanoTime();
    }

}
