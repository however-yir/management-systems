package io.howeveryir.cloudnativemall.common.core;

import java.util.List;

public record PageResult<T>(List<T> records, long total, int pageNo, int pageSize) {
}
