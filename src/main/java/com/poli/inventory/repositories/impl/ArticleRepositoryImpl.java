package com.poli.inventory.repositories.impl;

import com.poli.inventory.domain.*;
import com.poli.inventory.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ArticleRepositoryImpl implements ArticleRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Article> getArticles(String idCampus) {
        String sql = "SELECT a.*,r.name as nameRoom,r.idUser,s.name as nameState,t.name as nameTypeArticle FROM u280625412_inventory.articles as a \n" +
                "INNER JOIN u280625412_inventory.state as s on a.idState=s.idState\n" +
                "INNER JOIN u280625412_inventory.typearticle as t on a.idTypeArticle= t.idTypeArticle \n" +
                "INNER JOIN u280625412_inventory.rooms as r on a.idRoom=r.idRoom\n" +
                "WHERE a.idState=3 AND a.idCampus=?";
        List<Article> articles = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idCampus);
        for (Map row : rows) {
            Article newArticle = new Article();
            newArticle.setId((Integer) row.get("idArticle"));
            newArticle.setName((String) row.get("name"));
            newArticle.setSerial((String) row.get("serial"));
            State state = new State();
            state.setId((Integer) row.get("idState"));
            state.setName((String) row.get("nameState"));
            newArticle.setState(state);
            newArticle.setPhoto((byte[]) row.get("photo"));
            Room newRoom = new Room();
            newRoom.setIdRoom((Integer) row.get("idRoom"));
            newRoom.setName((String) row.get("nameRoom"));
            User user = new User();
            user.setIdUser((int) row.get("idUser"));
            newRoom.setUser(user);
            newArticle.setRoom(newRoom);
            TypeArticle newTypeArticle = new TypeArticle();
            newTypeArticle.setIdTypeArticle((Integer) row.get("idTypeArticle"));
            newTypeArticle.setName((String) row.get("nameTypeArticle"));
            newArticle.setTypeArticle(newTypeArticle);
            Campus campus = new Campus();
            campus.setIdCampus((int) row.get("idCampus"));
            newArticle.setCampus(campus);
            articles.add(newArticle);
        }
        return articles;
    }

    @Override
    public List<TypeArticle> getTypeArticle() {
        String sql = "SELECT * FROM u280625412_inventory.typearticle";
        List<TypeArticle> typeArticles = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map row : rows) {
            TypeArticle typeArticle = new TypeArticle();
            typeArticle.setIdTypeArticle((Integer) row.get("idTypeArticle"));
            typeArticle.setName((String) row.get("name"));
            typeArticles.add(typeArticle);
        }
        return typeArticles;
    }

    @Override
    public List<Article> consulArticleByRoom(Integer idRoom) {
        String sql = "SELECT a.*,r.name as nameRoom,r.idUser ,s.name as nameState,t.name as nameTypeArticle FROM u280625412_inventory.articles as a \n" +
                "INNER JOIN u280625412_inventory.state as s on a.idState=s.idState\n" +
                "INNER JOIN u280625412_inventory.typearticle as t on a.idTypeArticle= t.idTypeArticle \n" +
                "INNER JOIN u280625412_inventory.rooms as r on a.idRoom=r.idRoom\n" +
                "WHERE a.idRoom=?";
        List<Article> articles = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idRoom);
        for (Map row : rows) {
            Article newArticle = new Article();
            newArticle.setId((Integer) row.get("idArticle"));
            newArticle.setName((String) row.get("name"));
            newArticle.setSerial((String) row.get("serial"));
            State state = new State();
            state.setId((Integer) row.get("idState"));
            state.setName((String) row.get("nameState"));
            newArticle.setState(state);
            newArticle.setPhoto((byte[]) row.get("photo"));
            Room newRoom = new Room();
            newRoom.setIdRoom((Integer) row.get("idRoom"));
            newRoom.setName((String) row.get("nameRoom"));
            User user = new User();
            user.setIdUser((int) row.get("idUser"));
            newRoom.setUser(user);
            newArticle.setRoom(newRoom);
            TypeArticle newTypeArticle = new TypeArticle();
            newTypeArticle.setIdTypeArticle((Integer) row.get("idTypeArticle"));
            newTypeArticle.setName((String) row.get("nameTypeArticle"));
            newArticle.setTypeArticle(newTypeArticle);
            articles.add(newArticle);
        }
        return articles;
    }

    @Override
    public Article createArticle(Article article) {

        if (String.valueOf(article.getId()).equals("") || String.valueOf(article.getId()).equals("0")) {

            return create(article);

        } else {
            return update(article);
        }
    }

    private Article create(Article article) {
        KeyHolder holder = new GeneratedKeyHolder();
        String sql = "INSERT INTO u280625412_inventory.articles (name, serial, idState,photo,idTypeArticle,idCampus) VALUES (?, ?, ?, ?,?,?)";
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, article.getName());
                ps.setString(2, article.getSerial());
                ps.setInt(3, article.getState().getId());
                ps.setBinaryStream(4, getInputStreamImage(article.getPhoto()));
                ps.setInt(5, article.getTypeArticle().getIdTypeArticle());
                ps.setInt(6, article.getCampus().getIdCampus());
                return ps;
            }
        }, holder);

        Long key = holder.getKey().longValue();
        article.setId(key.intValue());
        return article;
    }

    private InputStream getInputStreamImage(byte[] image) {
        return new ByteArrayInputStream(image);
    }

    @Override
    public Article checkArticle(String serial) {
        String sql = "select * from u280625412_inventory.articles where serial = ?";

        List<Article> articles = jdbcTemplate.query(sql, new Object[]{serial}, new BeanPropertyRowMapper(Article.class));
        return articles.size() > 0 ? articles.get(0) : null;
    }

    @Override
    public Article update(Article article) {
        jdbcTemplate.update(
                "UPDATE u280625412_inventory.articles SET name=?, serial=?, idState=?, photo=? WHERE idArticle=?",
                article.getName(), article.getSerial(), article.getState().getId(), article.getPhoto(), article.getId());
        return article;
    }

    @Override
    public Article changeStateArticle(Article article) {
        jdbcTemplate.update(
                "UPDATE u280625412_inventory.articles SET idState=? WHERE idArticle=?",
                article.getState().getId(), article.getId());
        return article;
    }

    @Override
    public Article changeIdRoomArticle(Article article) {
        jdbcTemplate.update(
                "UPDATE u280625412_inventory.articles SET idRoom=? WHERE idArticle=?",
                article.getRoom().getIdRoom(), article.getId());
        return article;
    }

    @Override
    public Article scannerArticle(String idTypeArticle, String idCampus, String idRoom) {
        if (idRoom != "0") {
            return searchTheRooms(idTypeArticle,idCampus);
        }
        String sql = "SELECT a.idArticle,a.name AS nameArticle,a.serial,a.idState AS idStateArticle," +
                "sa.name AS nameStateArticle,a.idRoom,r.name AS nameRoom,r.idCampus,c.name AS nameCampus," +
                "r.idState AS idStateRoom, sr.name as nameStateRoom,t.idTypeArticle,t.name AS nameTypeArticle\n" +
                "FROM u280625412_inventory.articles AS a\n" +
                "INNER JOIN u280625412_inventory.state AS sa ON a.idState=sa.idState\n" +
                "INNER JOIN u280625412_inventory.rooms AS r ON a.idRoom=r.idRoom\n" +
                "INNER JOIN u280625412_inventory.state AS sr ON r.idState=sr.idState\n" +
                "INNER JOIN u280625412_inventory.typearticle AS t ON a.idTypeArticle=t.idTypeArticle\n" +
                "INNER JOIN u280625412_inventory.campus AS c ON a.idCampus=c.idCampus\n" +
                "WHERE a.idTypeArticle=? AND r.idCampus=? AND a.idState=3 AND a.idRoom=0";
        Article newArticle = new Article();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idTypeArticle, idCampus);
        for (Map row : rows) {
            newArticle.setId((Integer) row.get("idArticle"));
            newArticle.setName((String) row.get("nameArticle"));
            newArticle.setSerial((String) row.get("serial"));
            State stateArticle = new State();
            stateArticle.setId((Integer) row.get("idStateArticle"));
            stateArticle.setName((String) row.get("nameStateArticle"));
            newArticle.setState(stateArticle);
            Room newRoom = new Room();
            newRoom.setIdRoom((Integer) row.get("idRoom"));
            newRoom.setName((String) row.get("nameRoom"));
            State stateRoom = new State();
            stateRoom.setId((int) row.get("idStateRoom"));
            stateRoom.setName((String) row.get("nameStateRoom"));
            newRoom.setState(stateRoom);
            newArticle.setRoom(newRoom);
            TypeArticle newTypeArticle = new TypeArticle();
            newTypeArticle.setIdTypeArticle((Integer) row.get("idTypeArticle"));
            newTypeArticle.setName((String) row.get("nameTypeArticle"));
            newArticle.setTypeArticle(newTypeArticle);
            Campus campus = new Campus();
            campus.setIdCampus((int) row.get("idCampus"));
            campus.setName((String) row.get("nameCampus"));
            newArticle.setCampus(campus);

        }
        return (newArticle.equals("")) ? searchTheRooms(idTypeArticle, idCampus) : newArticle;
    }

    private Article searchTheRooms(String idTypeArticle, String idCampus) {
        String sql = "SELECT a.idArticle,a.name AS nameArticle,a.serial,a.idState AS idStateArticle," +
                "sa.name AS nameStateArticle,a.idRoom,r.name AS nameRoom,r.idCampus,c.name AS nameCampus," +
                "r.idState AS idStateRoom, sr.name as nameStateRoom,t.idTypeArticle,t.name AS nameTypeArticle\n" +
                "FROM u280625412_inventory.articles AS a\n" +
                "INNER JOIN u280625412_inventory.rooms AS r ON a.idRoom = r.idRoom\n" +
                "INNER JOIN u280625412_inventory.state AS sa ON a.idState=sa.idState\n" +
                "INNER JOIN u280625412_inventory.state AS sr ON r.idState=sr.idState\n" +
                "INNER JOIN u280625412_inventory.typearticle AS t ON a.idTypeArticle=t.idTypeArticle\n" +
                "INNER JOIN u280625412_inventory.campus AS c ON a.idCampus=c.idCampus\n" +
                "WHERE r.idState=3 AND a.idTypeArticle=2 AND a.idCampus=2\n";
        Article newArticle = new Article();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, idTypeArticle, idCampus);
        for (Map row : rows) {
            newArticle.setId((Integer) row.get("idArticle"));
            newArticle.setName((String) row.get("nameArticle"));
            newArticle.setSerial((String) row.get("serial"));
            State stateArticle = new State();
            stateArticle.setId((Integer) row.get("idStateArticle"));
            stateArticle.setName((String) row.get("nameStateArticle"));
            newArticle.setState(stateArticle);
            Room newRoom = new Room();
            newRoom.setIdRoom((Integer) row.get("idRoom"));
            newRoom.setName((String) row.get("nameRoom"));
            State stateRoom = new State();
            stateRoom.setId((int) row.get("idStateRoom"));
            stateRoom.setName((String) row.get("nameStateRoom"));
            newRoom.setState(stateRoom);
            newArticle.setRoom(newRoom);
            TypeArticle newTypeArticle = new TypeArticle();
            newTypeArticle.setIdTypeArticle((Integer) row.get("idTypeArticle"));
            newTypeArticle.setName((String) row.get("nameTypeArticle"));
            newArticle.setTypeArticle(newTypeArticle);
            Campus campus = new Campus();
            campus.setIdCampus((int) row.get("idCampus"));
            campus.setName((String) row.get("nameCampus"));
            newArticle.setCampus(campus);


        }
        return newArticle;
    }
}
