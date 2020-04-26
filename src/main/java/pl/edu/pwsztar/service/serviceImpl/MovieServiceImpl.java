package pl.edu.pwsztar.service.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pwsztar.domain.converter.Converter;
import pl.edu.pwsztar.domain.dto.CreateMovieDto;
import pl.edu.pwsztar.domain.dto.MovieCounterDto;
import pl.edu.pwsztar.domain.dto.MovieDto;
import pl.edu.pwsztar.domain.entity.Movie;
import pl.edu.pwsztar.domain.mapper.MovieListMapper;
import pl.edu.pwsztar.domain.mapper.MovieMapper;
import pl.edu.pwsztar.domain.repository.MovieRepository;
import pl.edu.pwsztar.service.MovieService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieServiceImpl.class);
    private static final String EMPTY = "";

    private final MovieRepository movieRepository;
    private final Converter<List<Movie>, List<MovieDto>> movieListMapper;
    private final Converter<CreateMovieDto, Movie> movieMapper;
    private final Converter<Long, MovieCounterDto> movieCounterMapper;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository,
                            MovieListMapper movieListMapper,
                            MovieMapper movieMapper,
                            Converter<Long, MovieCounterDto> movieCounterMapper) {

        this.movieRepository = movieRepository;
        this.movieListMapper = movieListMapper;
        this.movieMapper = movieMapper;
        this.movieCounterMapper = movieCounterMapper;
    }

    @Override
    public List<MovieDto> findAll() {
        List<Movie> movies = movieRepository.findAll();
        return movieListMapper.convert(movies);
    }

    @Override
    public List<MovieDto> findAllByYearDesc() {
        List<Movie> movies = movieRepository.findByOrderByYearDesc();
        return movieListMapper.convert(movies);
    }

    @Override
    public void createMovie(CreateMovieDto createMovieDto) {
        Movie movie = movieMapper.convert(createMovieDto);
        movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(Long movieId) {
        Optional<Movie> movieOptional = movieRepository.findById(movieId);
        movieOptional.ifPresent(movieRepository::delete);
    }

    @Override
    public MovieCounterDto countMovies() {
        return movieCounterMapper.convert(movieRepository.count());
    }

    @Override
    public List<MovieDto> searchMovies(String toSearch) {
        if (isAbleToSearch(toSearch)) {
            final List<Movie> searchedMovies = movieRepository.findByTitleAndYear(toSearch);
            return movieListMapper.convert(searchedMovies);
        }

        return Collections.emptyList();
    }

    private boolean isAbleToSearch(String toSearch) {
        return Optional.ofNullable(toSearch)
            .filter(text -> !text.isBlank())
            .isPresent();
    }
}
