package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;

import org.superbiz.moviefun.MoviesBean;

import java.util.Map;


@Controller
public class HomeController {

    private final MoviesBean moviesBean;

    private final PlatformTransactionManager moviesTransactionManager;
    private final PlatformTransactionManager albumsTransactionManager;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures, @Qualifier("movies") PlatformTransactionManager moviesTransactionManager, @Qualifier("albums") PlatformTransactionManager albumsTransactionManager) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.moviesTransactionManager = moviesTransactionManager;
        this.albumsTransactionManager =albumsTransactionManager;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    //@Transactional
    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        createMovies();
        createAlbums();

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }

    private void createAlbums() {
        TransactionStatus transaction = albumsTransactionManager.getTransaction(null);

        for (Album album : albumFixtures.load()) {
            albumsBean.addAlbum(album);
        }

        albumsTransactionManager.commit(transaction);
    }

    private void createMovies() {
        TransactionStatus transaction = moviesTransactionManager.getTransaction(null);

        for (Movie movie : movieFixtures.load()) {
            moviesBean.addMovie(movie);
        }

        moviesTransactionManager.commit(transaction);
    }
}
