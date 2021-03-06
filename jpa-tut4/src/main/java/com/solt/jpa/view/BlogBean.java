package com.solt.jpa.view;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.solt.jpa.entity.Blog;
import com.solt.jpa.entity.Blog.Status;
import com.solt.jpa.entity.Comment;
import com.solt.jpa.entity.User;
import com.solt.jpa.model.BlogModel;
import com.solt.jpa.view.common.ErrorHandler;
import com.solt.jpa.view.common.LoginUser;

@Named
@ViewScoped
public class BlogBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Blog blog;
    private String tags;
    private boolean publish;

    private String newComment;
    private Comment selectedComment;
    private String modalScript;
    
    @LoginUser
    @Inject
    private User loginUser;

    @Inject
    private BlogModel model;

    @PostConstruct
    public void init() {
    	String id = FacesContext.getCurrentInstance()
    				.getExternalContext()
    				.getRequestParameterMap().get("id");
    	if(null != id) {
    		blog = model.findBlogById(Long.parseLong(id));
    		publish = blog.getStatus().equals(Status.Published);
    		StringBuffer sb = new StringBuffer();
    		for(int i=0; i < blog.getTagList().size(); i++) {
    			if(i > 0) {
    				sb.append(",");
    			}
    			sb.append(blog.getTagList().get(i));
    		}
    		tags = sb.toString();
    	}
    }

    @ErrorHandler
    public String save() {
    	blog.setStatus(publish ? Status.Published: Status.Edit);
    	if(null != tags) {
    		Set<String> set = new HashSet<>(Arrays.asList(tags.split(",")));
    		blog.setTags(set);
    	}
    	model.saveBlog(blog);
    	return "/blog?faces-redirect=true&id=" + blog.getId();
    }

    @ErrorHandler
    public String addComment() {
    	Comment comment = new Comment();
    	comment.setComment(newComment);
    	comment.setUser(loginUser);
    	blog.addComment(comment);
    	model.saveBlog(blog);
    	return "/blog?faces-redirect=true&id=" + blog.getId();
    }

    @ErrorHandler
    public void editComment(Comment comment) {
    	this.selectedComment = comment;
    	modalScript = "$('#editComment').openModal();";
    }

    @ErrorHandler
    public String saveComment() {
    	
    	for(Comment cmt : blog.getCommentList()) {
    		if(cmt.getId() == selectedComment.getId()) {
    			cmt.setComment(selectedComment.getComment());
    			cmt.getSecurity().setModification(new Date());
    			cmt.getSecurity().setModUser(loginUser.getLoginId());
    	    	model.saveComment(cmt);
    		}
    	}
    	
    	return "/blog?faces-redirect=true&id=" + blog.getId();
    }

    @ErrorHandler
    public String deleteComment(Comment comment) {
    	blog.removeComment(comment);
    	model.saveBlog(blog);
    	return "/blog?faces-redirect=true&id=" + blog.getId();
    }

	public Blog getBlog() {
		return blog;
	}

	public void setBlog(Blog blog) {
		this.blog = blog;
	}

	public String getNewComment() {
		return newComment;
	}

	public void setNewComment(String newComment) {
		this.newComment = newComment;
	}

	public Comment getSelectedComment() {
		return selectedComment;
	}

	public void setSelectedComment(Comment selectedComment) {
		this.selectedComment = selectedComment;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	public String getModalScript() {
		return modalScript;
	}

	public void setModalScript(String modalScript) {
		this.modalScript = modalScript;
	}

}