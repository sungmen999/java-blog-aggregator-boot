package cz.jiripinkas.jba.service;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cz.jiripinkas.jba.dto.CategoryDto;
import cz.jiripinkas.jba.entity.Blog;
import cz.jiripinkas.jba.entity.Category;
import cz.jiripinkas.jba.repository.BlogRepository;
import cz.jiripinkas.jba.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private Mapper mapper;

	@Cacheable("categories")
	public List<Category> findAll() {
		List<Category> categories = categoryRepository.findAll();
		for (Category category : categories) {
			category.setBlogCount(blogRepository.countByCategoryId(category.getId()));
		}
		return categories;
	}

	@CacheEvict(value = "categories", allEntries = true)
	public void save(Category category) {
		categoryRepository.save(category);
	}

	@CacheEvict(value = "categories", allEntries = true)
	public void delete(int id) {
		categoryRepository.deleteById(id);
	}

	public CategoryDto findOneDto(int id) {
		return mapper.map(categoryRepository.findById(id).get(), CategoryDto.class);
	}

	@CacheEvict(value = "blogCountUnapproved", allEntries = true)
	public void addMapping(int blogId, int categoryId) {
		Category category = categoryRepository.findById(categoryId).get();
		Blog blog = blogRepository.findById(blogId).get();
		blog.setCategory(category);
		blogRepository.save(blog);
	}
	
}
