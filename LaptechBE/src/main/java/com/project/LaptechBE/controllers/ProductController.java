package com.project.LaptechBE.controllers;

import com.project.LaptechBE.DTO.ApiResponse;
import com.project.LaptechBE.DTO.ProductDTO.ProductDTO;
import com.project.LaptechBE.DTO.ValidationError;
import com.project.LaptechBE.services.ProductService;
import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.project.LaptechBE.untils.ProductValidate.validateObjectId;
import static com.project.LaptechBE.untils.ProductValidate.validateProductInput;

@Path(Endpoints.Product.BASE)
@Component
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductController {

    private final ProductService productService;

    @POST
    public Response createProduct(ProductDTO productDTO) {
        try{
            ValidationError validationError = (ValidationError) validateProductInput(productDTO);

            // Validate required fields including subCategory for laptops
            if (validationError != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.builder()
                                .status("ERR")
                                .message(validationError.getMessage())
                                .details(validationError.getDetails())
                                .build())
                        .build();

            }

            // Additional validation for subCategory
            if(productDTO.getCategory() == "laptop" && productDTO.getSubCategory().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.builder()
                                .status("ERR")
                                .message("Validation error")
                                .details("subCategory is required for laptop category")
                                .build())
                        .build();
            }

            var result = productService.createProduct(productDTO);

            if(result instanceof String){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message(result.toString())
                                        .build()
                        ).build();
            }

            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("Product created successfully")
                                    .data(result)
                                    .build()
                    ).build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.builder()
                            .status("ERR")
                            .message("Internal server error")
                            .details(e.getMessage())
                            .build())
                    .build();
        }
    }

//    @POST
//    @Path(Endpoints.Product.CREATEBULKPRODUCTS)
//    public Response createProductBulks(Product product) {
//
//    }
//
    @GET
    public Response getProducts(
            @QueryParam("category") String category,
            @QueryParam("subCategory") String subCategory,
            @QueryParam("isFeatured") Boolean isFeatured,
            @QueryParam("brand") String brand,
            @QueryParam("minPrice") Double minPrice,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("search") String search,
            @QueryParam("sort") Integer sort,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("limit") @DefaultValue("10") int limit
    ) {
        try{
            // 1. Xây dựng filters từ query parameters
            Map<String, Object> filters = new HashMap<>();
            if (category != null && !category.isEmpty()) filters.put("category", category);
            if (subCategory != null && !subCategory.isEmpty()) filters.put("subCategory", subCategory);
            if (brand != null && !brand.isEmpty()) filters.put("brand", brand);

            if (minPrice != null || maxPrice != null) {
                Map<String, Double> priceFilter = new HashMap<>();
                if (minPrice != null) priceFilter.put("$gte", minPrice);
                if (maxPrice != null) priceFilter.put("$lte", maxPrice);
                filters.put("price", priceFilter);
            }

            if (search != null && !brand.isEmpty()) filters.put("search", search);
            if (sort != null) filters.put("sort", sort);
            if (isFeatured != null) filters.put("isFeatured", isFeatured);

            var result = productService.getProducts(filters,page,limit);
            int count = ((Long) result.get("count")).intValue();
            Integer totalPages = 0;
            if(result.get("totalPages") == null && count>0){
                if(count<10 && count>0){
                    totalPages = 1;
                }
                else{
                    totalPages = (Integer)result.get("totalPages");
                }
            }
            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .data(result.get("data"))
                                    .count(count)
                                    .totalPages(totalPages)
                                    .build()
                    )
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Failed to fetch products")
                                    .details(e.getMessage()).build()
                    )
                    .build();
        }
    }

    @GET
    @Path(Endpoints.Product.GETALLCATEGORY)
    public Response getAllCategory() {
        try{
            var result = productService.getAllCategory();
            return Response.status(200)
                    .entity(
                        ApiResponse.builder()
                                .status("OK")
                                .data(result)
                                .build()
                    ).build();

        } catch (Exception e) {
            return Response.status(500)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Failed to fetch product")
                                    .details(e.getMessage())
                                    .build()
                    ).build();
        }
    }

    @GET
    @Path(Endpoints.Product.GETPRODUCTBYID+"{id}")
    public Response getProductById(@PathParam("id") String id) {
        try{
            if(validateObjectId(id)){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Invalid product ID format").build()
                        ).build();
            }

            var result = productService.getProductById(id);

            if(result instanceof String ){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message((String) result)
                                        .build()
                        )
                        .build();
            }

            return Response.status(Response.Status.OK)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .data(result)
                                    .build()
                    )
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Failed to fetch product")
                                    .details(e.toString())
                                    .build()
                    )
                    .build();
        }
    }

    @PUT
    @Path(Endpoints.Product.UPDATEPRODUCT+"{id}")
    public Response updateProduct(@PathParam("id") String id,ProductDTO productDTO) {
        try{
            if(validateObjectId(id)){
                return Response.status(400)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Invalid product ID format")
                                        .build()
                        ).build();
            }

            ValidationError validationError = (ValidationError) validateProductInput(productDTO,true);

            if(validationError != null){
                return Response.status(400)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message(validationError.getMessage())
                                        .details(validationError.getDetails())
                                        .build()
                        ).build();
            }

            var result = productService.updateProduct(id,productDTO);

            if(result instanceof String ){
                return Response.status(400)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message((String) result)
                                        .build()
                        )
                        .build();
            }
            return Response.status(200)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("Product updated successfully")
                                    .data(result)
                                    .build()
                    ).build();
        } catch (Exception e) {
            return Response.status(400)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message(e.getMessage())
                                    .build()

                    )
                    .build();
    }
//
//    @DELETE
//    @Path(Endpoints.Product.DELETEPRODUCT+"{id}")
//    public Response deleteProduct(@PathParam("id") String id) {
//
//    }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") String id) {
        try{
            if(validateObjectId(id)){
                return Response.status(400)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message("Invalid product ID format")
                                        .build()
                        ).build();
            }

            var product = productService.deleteProduct(id);

            if(product instanceof String){
                return Response.status(404)
                        .entity(
                                ApiResponse.builder()
                                        .status("ERR")
                                        .message(product.toString())
                                        .build()
                        ).build();
            }

            return Response.status(200)
                    .entity(
                            ApiResponse.builder()
                                    .status("OK")
                                    .message("Product deleted successfully")
                                    .build()
                    ).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity(
                            ApiResponse.builder()
                                    .status("ERR")
                                    .message("Failed to fetch product")
                                    .details(e.getMessage())
                                    .build()
                    ).build();
        }
    }
}
