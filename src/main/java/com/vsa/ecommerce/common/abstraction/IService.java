package com.vsa.ecommerce.common.abstraction;

/**
 * Generic service interface defining the contract for business logic execution.
 * @param <TRequest> The request type, extending IRequest.
 * @param <TResponse> The response type, extending IResponse.
 */
public interface IService<TRequest extends Request, TResponse extends Response> {
    TResponse execute(TRequest request);
}
