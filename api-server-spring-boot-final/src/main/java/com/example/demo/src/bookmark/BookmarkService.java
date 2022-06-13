package com.example.demo.src.bookmark;

import com.example.demo.config.BaseException;
import com.example.demo.src.bookmark.model.BookmarkedStatus;
import com.example.demo.src.bookmark.model.GetBookmarkCountRes;
import com.example.demo.src.bookmark.model.PostBookmarkRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class BookmarkService {
    private final BookmarkProvider provider;
    private final BookmarkDao dao;

    final Logger logger = LoggerFactory.getLogger(BookmarkService.class);

    @Autowired
    public BookmarkService(BookmarkProvider provider, BookmarkDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer postBookmark(int userId, String contentsType, int contentsId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkContentId(contentsType, contentsId) == 0) {
            throw new BaseException(BOOKMARKS_NOT_EXISTS_CONTENT);
        }
        if(provider.checkBookmarked(userId, contentsType, contentsId) == 1) {
            throw new BaseException(BOOKMARKS_ALREADY_BOOKMARKED);
        }
        try {
            if(provider.checkUnmarked(userId, contentsType, contentsId) == 0)
                return dao.createBookmark(userId, contentsType, contentsId);
            else
                return dao.postBookmark(userId, contentsType, contentsId);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Integer cancelBookmark(int userId, String contentsType, int contentsId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkContentId(contentsType, contentsId) == 0) {
            throw new BaseException(BOOKMARKS_NOT_EXISTS_CONTENT);
        }
        if(provider.checkUnmarked(userId, contentsType, contentsId) == 1) {
            throw new BaseException(BOOKMARKS_ALREADY_UNMARKED);
        }
        try {
            return dao.cancelBookmark(userId, contentsType, contentsId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public BookmarkedStatus classifyContentsType(List<GetBookmarkCountRes> getBookmarkCountRes) throws BaseException {
        BookmarkedStatus bookmarkedStatus = new BookmarkedStatus(new GetBookmarkCountRes("top_lists",0,""),
                new GetBookmarkCountRes("mylists",0,""), new GetBookmarkCountRes("mango_pick_stories",0,""));
        if(getBookmarkCountRes == null) {
            return bookmarkedStatus;
        }
        for(GetBookmarkCountRes element : getBookmarkCountRes){
            if(element.getContentsType().equals("top_lists")) bookmarkedStatus.setTopList(element);
            if(element.getContentsType().equals("mylists")) bookmarkedStatus.setMyList(element);
        }

        return bookmarkedStatus;
    }

}
