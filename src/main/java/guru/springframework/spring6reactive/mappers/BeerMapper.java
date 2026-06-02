package guru.springframework.spring6reactive.mappers;

import guru.springframework.spring6reactive.domain.Beer;
import guru.springframework.spring6reactive.model.BeerDTO;
import org.mapstruct.Mapper;
/**
 * Created by jt, Spring Framework Guru.
 */
@Mapper(componentModel = "spring") // Spring-managed bean
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDto(Beer beer);
}
