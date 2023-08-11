package cn.addenda.bc.bc.sc.multidatasource;

import lombok.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2022/3/3 17:26
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MultiDataSourceEntry {

    private DataSource master;

    private List<DataSource> slaves = new ArrayList<>();

}
